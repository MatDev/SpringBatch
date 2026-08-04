package com.xanxs.engine_core_job.job.factory;
import com.xanxs.engine_core_job.api.dto.JobRunRequest;
import com.xanxs.engine_core_job.job.registry.BatchJobRegistry;
import com.xanxs.engine_core_job.tracker.ExecutionTracker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobFactory {

    private final BatchJobRegistry jobRegistry;
    private final JobLauncher jobLauncher;
    private final ExecutionTracker tracker;

    // Lanza el job de forma asíncrona y retorna el executionId inmediatamente
    public String launch(JobRunRequest request) {
        String executionId = UUID.randomUUID().toString();

        // Validar que el job existe antes de lanzar
        if (!jobRegistry.exists(request.getJobName())) {
            throw new IllegalArgumentException(
                    String.format("Job '%s' no registrado en el core",
                            request.getJobName())
            );
        }

        // Crear registro de ejecución en BD
        tracker.create(
                executionId,
                request.getJobName(),
                request.getTemplateId(),
                request.getFilePath()
        );

        // Lanzar de forma asíncrona
        launchAsync(request, executionId);

        log.info("Job '{}' lanzado con executionId: {}",
                request.getJobName(), executionId);

        return executionId;
    }

    @Async("jobTaskExecutor")
    protected void launchAsync(JobRunRequest request, String executionId) {
        try {
            // Construir el Job
            Job job = jobRegistry.get(request.getJobName())
                    .buildJob(request, executionId);

            // Parámetros únicos para permitir relanzar el mismo job
            JobParameters params = new JobParametersBuilder()
                    .addString("executionId", executionId)
                    .addString("filePath", request.getFilePath())
                    .addLong("templateId", request.getTemplateId())
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            jobLauncher.run(job, params);

        } catch (Exception ex) {
            log.error("Error ejecutando job '{}': {}",
                    request.getJobName(), ex.getMessage(), ex);
            tracker.markFailed(executionId, ex.getMessage());
        }
    }
}