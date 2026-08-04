package com.xanxs.engine_core.dto.request;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CoreJobRequest {
    private String jobName;
    private Long templateId;
    private String filePath;
}