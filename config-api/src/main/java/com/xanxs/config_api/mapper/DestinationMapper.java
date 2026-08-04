package com.xanxs.config_api.mapper;




import com.xanxs.config_api.domain.entity.Destination;
import com.xanxs.config_api.domain.entity.DestinationMapping;
import com.xanxs.config_api.dto.request.DestinationMappingRequest;
import com.xanxs.config_api.dto.request.DestinationRequest;
import com.xanxs.config_api.dto.response.DestinationMappingResponse;
import com.xanxs.config_api.dto.response.DestinationResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DestinationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "template", ignore = true)
    @Mapping(target = "mappings", ignore = true)
    Destination toEntity(DestinationRequest request);

    DestinationResponse toResponse(Destination entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "template", ignore = true)
    @Mapping(target = "mappings", ignore = true)
    void updateEntity(@MappingTarget Destination entity,
                      DestinationRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "destination", ignore = true)
    DestinationMapping toMappingEntity(DestinationMappingRequest request);

    DestinationMappingResponse toMappingResponse(DestinationMapping entity);
}