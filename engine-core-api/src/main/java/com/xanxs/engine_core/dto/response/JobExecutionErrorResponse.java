package com.xanxs.engine_core.dto.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class JobExecutionErrorResponse {
    private Long id;
    private Integer lineNumber;
    private String lineContent;
    private String errorDescription;
    private String exceptionDetail;
    private LocalDateTime createdAt;
}
