package com.xanxs.engine_core.dto.response;


import lombok.Data;

@Data
public class CoreJobResponse {
    private String coreExecutionId;
    private String status;
    private String message;
}