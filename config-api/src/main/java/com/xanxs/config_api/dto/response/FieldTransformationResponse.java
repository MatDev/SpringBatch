package com.xanxs.config_api.dto.response;



import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class FieldTransformationResponse {
    private Long id;
    private String transformerName;
    private Map<String, String> parameters;
    private Integer orderIndex;
}