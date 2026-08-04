package com.xanxs.engine_core_job.domain.model;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FieldConfig {
    private Long id;
    private String name;
    private String fieldType;       // STRING | INTEGER | LONG | DECIMAL | DATE | BOOLEAN
    private Integer position;       // inicio en FIXED, índice en DELIMITED
    private Integer length;         // solo FIXED
    private Boolean required;
    private String defaultValue;
    private Integer orderIndex;
    private List<TransformationConfig> transformations;
}
