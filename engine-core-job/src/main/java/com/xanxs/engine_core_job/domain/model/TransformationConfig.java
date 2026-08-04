package com.xanxs.engine_core_job.domain.model;


import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class TransformationConfig {
    private Long id;
    private String transformerName;
    private Map<String, String> parameters;
    private Integer orderIndex;
}
