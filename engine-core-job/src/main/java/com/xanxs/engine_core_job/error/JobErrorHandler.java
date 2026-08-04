package com.xanxs.engine_core_job.error;

import com.xanxs.engine_core_job.domain.entity.CoreError;
import com.xanxs.engine_core_job.domain.entity.CoreExecution;
import com.xanxs.engine_core_job.domain.repository.CoreErrorRepository;
import com.xanxs.engine_core_job.domain.repository.CoreExecutionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobErrorHandler {

    private final CoreErrorRepository errorRepository;
    private final CoreExecutionRepository executionRepository;

    // Propagation.REQUIRES_NEW — transacción independiente
    // para que el error se guarde aunque el chunk falle
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void register(String executionId,
                         Integer lineNumber,
                         String lineContent,
                         String errorDescription,
                         String exceptionDetail) {

        CoreExecution execution = executionRepository
                .findById(executionId)
                .orElseThrow(() -> new IllegalStateException(
                        "Ejecución no encontrada: " + executionId));

        CoreError error = CoreError.builder()
                .execution(execution)
                .lineNumber(lineNumber)
                .lineContent(lineContent)
                .errorDescription(errorDescription)
                .exceptionDetail(exceptionDetail)
                .build();

        errorRepository.save(error);

        log.debug("Error registrado - línea {}: {}", lineNumber, errorDescription);
    }
}