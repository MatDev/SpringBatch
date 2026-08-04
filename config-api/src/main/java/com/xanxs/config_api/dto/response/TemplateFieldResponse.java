package com.xanxs.config_api.dto.response;




import com.xanxs.config_api.domain.enums.FieldType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TemplateFieldResponse {
    private Long id;
    private String name;
    private FieldType fieldType;
    private Integer position;
    private Integer length;
    private Boolean required;
    private String defaultValue;
    private Integer orderIndex;
    private List<FieldTransformationResponse> transformations;
}