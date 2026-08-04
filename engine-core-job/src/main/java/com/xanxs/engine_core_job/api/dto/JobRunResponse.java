package com.xanxs.engine_core_job.api.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobRunResponse {
    private String coreExecutionId;
    private String status;
    private String message;
}