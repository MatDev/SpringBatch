package com.xanxs.config_api.mapper;

// mapper/TemplateFieldMapper.java

import com.xanxs.config_api.domain.entity.TemplateField;
import com.xanxs.config_api.dto.request.TemplateFieldRequest;
import com.xanxs.config_api.dto.response.TemplateFieldResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = FieldTransformationMapper.class)
public interface TemplateFieldMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "template", ignore = true)
    @Mapping(target = "transformations", ignore = true)
    TemplateField toEntity(TemplateFieldRequest request);

    // MapStruct mapea transformations automáticamente usando FieldTransformationMapper
    TemplateFieldResponse toResponse(TemplateField entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "template", ignore = true)
    @Mapping(target = "transformations", ignore = true)
    void updateEntity(@MappingTarget TemplateField entity,
                      TemplateFieldRequest request);
}