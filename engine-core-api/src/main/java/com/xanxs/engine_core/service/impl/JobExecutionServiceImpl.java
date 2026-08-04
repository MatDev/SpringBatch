package com.xanxs.engine_core.service.impl;

// service/impl/JobExecutionServiceImpl.java
import com.xanxs.engine_core.client.BatchEngineCoreClient;
import com.xanxs.engine_core.domain.entity.JobExecution;
import com.xanxs.engine_core.domain.enums.JobExecutionStatus;
import com.xanxs.engine_core.dto.request.CoreJobRequest;
import com.xanxs.engine_core.dto.request.JobExecutionRequest;
import com.xanxs.engine_core.dto.response.*;
import com.xanxs.engine_core.exception.BusinessException;
import com.xanxs.engine_core.exception.ResourceNotFoundException;
import com.xanxs.engine_core.mapper.JobExecutionMapper;
import com.xanxs.engine_core.repository.JobExecutionErrorRepository;
import com.xanxs.engine_core.repository.JobExecutionRepository;
import com.xanxs.engine_core.service.JobExecutionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobExecutionServiceImpl implements JobExecutionService {

    private final JobExecutionRepository executionRepository;
    private final JobExecutionErrorRepository errorRepository;
    private final JobExecutionMapper executionMapper;
    private final BatchEngineCoreClient coreClient;

    @Override
    @Transactional
    public JobExecutionResponse execute(JobExecutionRequest request) {
        log.debug("Lanzando job '{}' con plantilla id: {}", request.getJobName(),
                request.getTemplateId());

        // 1. Guardar ejecución en estado PENDING
        JobExecution execution = JobExecution.builder()
                .jobName(request.getJobName())
                .templateId(request.getTemplateId())
                .filePath(request.getFilePath())
                .requestedBy(request.getRequestedBy())
                .status(JobExecutionStatus.PENDING)
                .build();

        execution = executionRepository.save(execution);

        try {
            // 2. Llamar al core
            CoreJobRequest coreRequest = CoreJobRequest.builder()
                    .jobName(request.getJobName())
                    .templateId(request.getTemplateId())
                    .filePath(request.getFilePath())
                    .build();

            CoreJobResponse coreResponse = coreClient.runJob(coreRequest);

            // 3. Actualizar con respuesta del core
            execution.setCoreExecutionId(coreResponse.getCoreExecutionId());
            execution.setStatus(JobExecutionStatus.RUNNING);
            execution.setStartedAt(LocalDateTime.now());

            log.info("Job '{}' lanzado en core con id: {}",
                    request.getJobName(), coreResponse.getCoreExecutionId());

        } catch (Exception ex) {
            // Si el core falla, marcamos como FAILED
            execution.setStatus(JobExecutionStatus.FAILED);
            execution.setFinishedAt(LocalDateTime.now());
            log.error("Error al lanzar job '{}' en el core: {}",
                    request.getJobName(), ex.getMessage());
        }

        JobExecution saved = executionRepository.save(execution);
        return executionMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public JobExecutionResponse findById(Long id) {
        return executionMapper.toResponse(getExecutionOrThrow(id));
    }

    @Override
    @Transactional
    public JobExecutionResponse refreshStatus(Long id) {
        log.debug("Sincronizando estado de ejecución id: {}", id);

        JobExecution execution = getExecutionOrThrow(id);

        // Solo sincronizar si está en estado activo
        if (execution.getCoreExecutionId() == null) {
            throw new BusinessException(
                    "La ejecución no tiene un id del core asociado"
            );
        }

        if (execution.getStatus() == JobExecutionStatus.COMPLETED
                || execution.getStatus() == JobExecutionStatus.FAILED) {
            log.debug("Ejecución id: {} ya está en estado final, no se sincroniza", id);
            return executionMapper.toResponse(execution);
        }

        // Consultar estado al core
        CoreJobStatusResponse coreStatus =
                coreClient.getStatus(execution.getCoreExecutionId());

        // Actualizar campos
        execution.setTotalRecords(coreStatus.getTotalRecords());
        execution.setSuccessRecords(coreStatus.getSuccessRecords());
        execution.setErrorRecords(coreStatus.getErrorRecords());

        JobExecutionStatus newStatus = mapCoreStatus(coreStatus.getStatus());
        execution.setStatus(newStatus);

        if (coreStatus.getFinishedAt() != null) {
            execution.setFinishedAt(coreStatus.getFinishedAt());
        }

        JobExecution updated = executionRepository.save(execution);
        log.info("Estado de ejecución id: {} actualizado a: {}", id, newStatus);

        return executionMapper.toResponse(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobExecutionResponse> findAll() {
        return executionRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(executionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobExecutionResponse> findAllByStatus(String status) {
        JobExecutionStatus jobStatus;
        try {
            jobStatus = JobExecutionStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Estado inválido: " + status);
        }
        return executionRepository.findAllByStatusOrderByCreatedAtDesc(jobStatus)
                .stream()
                .map(executionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobExecutionErrorResponse> findErrors(Long id) {
        getExecutionOrThrow(id);
        return errorRepository
                .findAllByJobExecutionIdOrderByLineNumberAsc(id)
                .stream()
                .map(executionMapper::toErrorResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public JobReportResponse getReport(Long id) {
        JobExecution execution = executionRepository.findByIdWithErrors(id)
                .orElseThrow(() -> new ResourceNotFoundException("JobExecution", id));

        JobReportResponse report = executionMapper.toReportResponse(execution);

        // Calcular duración
        if (execution.getStartedAt() != null && execution.getFinishedAt() != null) {
            report.setDurationSeconds(
                    Duration.between(execution.getStartedAt(), execution.getFinishedAt())
                            .toSeconds()
            );
        }

        // Calcular tasa de éxito
        if (execution.getTotalRecords() != null && execution.getTotalRecords() > 0) {
            double rate = (execution.getSuccessRecords() * 100.0) / execution.getTotalRecords();
            report.setSuccessRate(Math.round(rate * 100.0) / 100.0);
        }

        // Mapear errores
        report.setErrors(executionMapper.toErrorResponseList(execution.getErrors()));

        return report;
    }

    // --- Helpers privados ---

    private JobExecution getExecutionOrThrow(Long id) {
        return executionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("JobExecution", id));
    }

    private JobExecutionStatus mapCoreStatus(String coreStatus) {
        return switch (coreStatus.toUpperCase()) {
            case "STARTED", "RUNNING" -> JobExecutionStatus.RUNNING;
            case "COMPLETED" -> JobExecutionStatus.COMPLETED;
            case "FAILED" -> JobExecutionStatus.FAILED;
            case "STOPPED" -> JobExecutionStatus.STOPPED;
            default -> JobExecutionStatus.RUNNING;
        };
    }
}