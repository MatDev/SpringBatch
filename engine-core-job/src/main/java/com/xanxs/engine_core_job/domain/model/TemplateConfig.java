package com.xanxs.engine_core_job.domain.model;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TemplateConfig {
    private Long id;
    private String name;
    private String fileType;         // FIXED | DELIMITED
    private String separator;
    private String encoding;
    private Boolean skipEmptyLines;
    private String onError;          // STOP | CONTINUE
    private List<FieldConfig> fields;
    private List<DestinationConfig> destinations;
}