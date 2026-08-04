package com.xanxs.engine_core_job.domain.model;


import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class DestinationConfig {
    private Long id;
    private String destinationType;  // POSTGRESQL | MONGODB | KAFKA | FILE | REST
    private String name;
    private String targetTable;
    private String targetCollection;
    private String targetTopic;
    private String targetPath;
    private String targetUrl;
    private Map<String, Object> extraConfig;
    private Map<String, String> mappings; // fieldName → targetColumn
}
