package com.xanxs.engine_core_job.job.definition;

import com.xanxs.engine_core_job.api.dto.JobRunRequest;
import com.xanxs.engine_core_job.domain.model.ProcessedRecord;
import com.xanxs.engine_core_job.domain.model.TemplateConfig;
import com.xanxs.engine_core_job.error.JobErrorHandler;
import com.xanxs.engine_core_job.job.BatchJobDefinition;
import com.xanxs.engine_core_job.processor.FieldExtractor;
import com.xanxs.engine_core_job.processor.validator.RecordProcessor;
import com.xanxs.engine_core_job.processor.validator.RecordValidator;
import com.xanxs.engine_core_job.reader.FilePartitioner;
import com.xanxs.engine_core_job.reader.RangedFlatFileItemReader;
import com.xanxs.engine_core_job.template.TemplateLoader;
import com.xanxs.engine_core_job.tracker.ExecutionTracker;
import com.xanxs.engine_core_job.writer.CompositeDestinationWriter;
import com.xanxs.engine_core_job.writer.WriterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.partition.support.TaskExecutorPartitionHandler;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Component
public class GenericFileJob implements BatchJobDefinition {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final TemplateLoader templateLoader;
    private final FieldExtractor fieldExtractor;
    private final RecordValidator validator;
    private final WriterRegistry writerRegistry;
    private final JobErrorHandler errorHandler;
    private final ExecutionTracker tracker;
    private final TaskExecutor stepExecutor;

    private static final int CHUNK_SIZE = 1000;
    private static final int GRID_SIZE = 8;

    // Constructor manual — @Qualifier solo funciona aquí
    public GenericFileJob(JobRepository jobRepository,
                          PlatformTransactionManager transactionManager,
                          TemplateLoader templateLoader,
                          FieldExtractor fieldExtractor,
                          RecordValidator validator,
                          WriterRegistry writerRegistry,
                          JobErrorHandler errorHandler,
                          ExecutionTracker tracker,
                          @Qualifier("batchStepExecutor") TaskExecutor stepExecutor) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.templateLoader = templateLoader;
        this.fieldExtractor = fieldExtractor;
        this.validator = validator;
        this.writerRegistry = writerRegistry;
        this.errorHandler = errorHandler;
        this.tracker = tracker;
        this.stepExecutor = stepExecutor;
    }

    @Override
    public String getJobName() {
        return "genericFileJob";
    }

    @Override
    public Job buildJob(JobRunRequest request, String executionId) {
        TemplateConfig template = templateLoader.load(request.getTemplateId());
        Step workerStep = buildWorkerStep(request, template, executionId);
        Step masterStep = buildMasterStep(request, template, workerStep);

        return new JobBuilder(executionId, jobRepository)
                .listener(buildJobListener(executionId))
                .start(masterStep)
                .build();
    }

    private Step buildWorkerStep(JobRunRequest request,
                                 TemplateConfig template,
                                 String executionId) {
        return new StepBuilder("workerStep-" + executionId, jobRepository)
                .<String, ProcessedRecord>chunk(CHUNK_SIZE, transactionManager)
                .reader(buildReader(request.getFilePath(), 1, Long.MAX_VALUE, template))
                .processor(new RecordProcessor(template, fieldExtractor, validator, 0))
                .writer(new CompositeDestinationWriter(
                        template, writerRegistry, errorHandler, executionId))
                .faultTolerant()
                .skipLimit(Integer.MAX_VALUE)
                .skip(Exception.class)
                .build();
    }

    private Step buildMasterStep(JobRunRequest request,
                                 TemplateConfig template,
                                 Step workerStep) {
        FilePartitioner partitioner = new FilePartitioner(
                request.getFilePath(),
                Boolean.TRUE.equals(template.getSkipEmptyLines())
        );

        TaskExecutorPartitionHandler partitionHandler =
                new TaskExecutorPartitionHandler();
        partitionHandler.setStep(workerStep);
        partitionHandler.setTaskExecutor(stepExecutor);
        partitionHandler.setGridSize(GRID_SIZE);

        return new StepBuilder("masterStep", jobRepository)
                .partitioner("workerStep", partitioner)
                .partitionHandler(partitionHandler)
                .build();
    }

    private RangedFlatFileItemReader buildReader(String filePath,
                                                 long startLine,
                                                 long endLine,
                                                 TemplateConfig template) {
        return new RangedFlatFileItemReader(filePath, startLine, endLine, template);
    }

    private org.springframework.batch.core.JobExecutionListener buildJobListener(
            String executionId) {
        return new org.springframework.batch.core.JobExecutionListener() {
            @Override
            public void afterJob(org.springframework.batch.core.JobExecution jobExecution) {
                int total = (int) jobExecution.getStepExecutions().stream()
                        .mapToLong(s -> s.getReadCount())
                        .sum();
                int errors = (int) jobExecution.getStepExecutions().stream()
                        .mapToLong(s -> s.getSkipCount())
                        .sum();
                int success = total - errors;

                if (jobExecution.getStatus().isUnsuccessful()) {
                    tracker.markFailed(executionId,
                            jobExecution.getAllFailureExceptions()
                                    .stream()
                                    .findFirst()
                                    .map(Throwable::getMessage)
                                    .orElse("Error desconocido"));
                } else {
                    tracker.markCompleted(executionId, total, success, errors);
                }
            }
        };
    }
}