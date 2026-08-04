package com.xanxs.engine_core_job.job;


import com.xanxs.engine_core_job.api.dto.JobRunRequest;
import org.springframework.batch.core.Job;

public interface BatchJobDefinition {

    // Nombre con el que se registra en el JobRegistry
    String getJobName();

    // Construye el Job con los parámetros de la solicitud
    Job buildJob(JobRunRequest request, String executionId);
}