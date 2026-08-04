package com.xanxs.engine_core.dto.response;

// dto/response/JobReportResponse.java
import com.xanxs.engine_core.domain.enums.JobExecutionStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class JobReportResponse {
    private Long executionId;
    private String jobName;
    private String filePath;
    private JobExecutionStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
    private Long durationSeconds;      // tiempo total en segundos
    private Integer totalRecords;
    private Integer successRecords;
    private Integer errorRecords;
    private Double successRate;        // porcentaje de éxito
    private List<JobExecutionErrorResponse> errors;
}
