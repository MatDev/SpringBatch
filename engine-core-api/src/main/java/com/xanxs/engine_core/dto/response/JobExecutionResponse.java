package com.xanxs.engine_core.dto.response;


import com.xanxs.engine_core.domain.enums.JobExecutionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class JobExecutionResponse {
    private Long id;
    private String jobName;
    private Long templateId;
    private String filePath;
    private JobExecutionStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Integer totalRecords;
    private Integer successRecords;
    private Integer errorRecords;
    private String requestedBy;
    private String coreExecutionId;
    private LocalDateTime createdAt;
}