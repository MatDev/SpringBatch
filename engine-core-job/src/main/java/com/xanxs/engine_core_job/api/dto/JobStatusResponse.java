package com.xanxs.engine_core_job.api.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class JobStatusResponse {
    private String coreExecutionId;
    private String jobName;
    private String status;
    private Integer totalRecords;
    private Integer successRecords;
    private Integer errorRecords;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
}