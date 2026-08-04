package com.xanxs.config_api.mapper;




import com.xanxs.config_api.domain.entity.Template;
import com.xanxs.config_api.dto.request.TemplateRequest;
import com.xanxs.config_api.dto.response.TemplateResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        uses = {TemplateFieldMapper.class, DestinationMapper.class})
public interface TemplateMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "fields", ignore = true)
    @Mapping(target = "destinations", ignore = true)
    Template toEntity(TemplateRequest request);

    // fields y destinations se mapean automáticamente
    // usando TemplateFieldMapper y DestinationMapper
    TemplateResponse toResponse(Template entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "fields", ignore = true)
    @Mapping(target = "destinations", ignore = true)
    void updateEntity(@MappingTarget Template entity,
                      TemplateRequest request);
}