package com.xanxs.engine_core.dto.response;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CoreJobStatusResponse {
    private String coreExecutionId;
    private String status;
    private Integer totalRecords;
    private Integer successRecords;
    private Integer errorRecords;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;
}
