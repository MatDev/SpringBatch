package com.xanxs.engine_core_job.tracker;

import com.xanxs.engine_core_job.domain.entity.CoreExecution;
import com.xanxs.engine_core_job.domain.repository.CoreExecutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutionTracker {

    private final CoreExecutionRepository executionRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CoreExecution create(String executionId,
                                String jobName,
                                Long templateId,
                                String filePath) {
        CoreExecution execution = CoreExecution.builder()
                .id(executionId)
                .jobName(jobName)
                .templateId(templateId)
                .filePath(filePath)
                .status("RUNNING")
                .startedAt(LocalDateTime.now())
                .totalRecords(0)
                .successRecords(0)
                .errorRecords(0)
                .build();

        CoreExecution saved = executionRepository.save(execution);
        log.info("Ejecución creada: {}", executionId);
        return saved;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markCompleted(String executionId,
                              int totalRecords,
                              int successRecords,
                              int errorRecords) {
        executionRepository.findById(executionId).ifPresent(execution -> {
            execution.setStatus("COMPLETED");
            execution.setFinishedAt(LocalDateTime.now());
            execution.setTotalRecords(totalRecords);
            execution.setSuccessRecords(successRecords);
            execution.setErrorRecords(errorRecords);
            executionRepository.save(execution);
            log.info("Ejecución {} completada — total: {}, ok: {}, errores: {}",
                    executionId, totalRecords, successRecords, errorRecords);
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markFailed(String executionId, String errorMessage) {
        executionRepository.findById(executionId).ifPresent(execution -> {
            execution.setStatus("FAILED");
            execution.setFinishedAt(LocalDateTime.now());
            execution.setErrorMessage(errorMessage);
            executionRepository.save(execution);
            log.error("Ejecución {} fallida: {}", executionId, errorMessage);
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateCounters(String executionId,
                               int totalRecords,
                               int successRecords,
                               int errorRecords) {
        executionRepository.findById(executionId).ifPresent(execution -> {
            execution.setTotalRecords(totalRecords);
            execution.setSuccessRecords(successRecords);
            execution.setErrorRecords(errorRecords);
            executionRepository.save(execution);
        });
    }
}