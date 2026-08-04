package com.xanxs.config_api.mapper;


import com.xanxs.config_api.domain.entity.FieldTransformation;
import com.xanxs.config_api.dto.request.FieldTransformationRequest;
import com.xanxs.config_api.dto.response.FieldTransformationResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FieldTransformationMapper {

    FieldTransformation toEntity(FieldTransformationRequest request);

    FieldTransformationResponse toResponse(FieldTransformation entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget FieldTransformation entity,
                      FieldTransformationRequest request);
}
