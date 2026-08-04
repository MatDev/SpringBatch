package com.xanxs.config_api.service;

// service/DestinationService.java

import com.xanxs.config_api.dto.request.DestinationMappingRequest;
import com.xanxs.config_api.dto.request.DestinationRequest;
import com.xanxs.config_api.dto.response.DestinationResponse;

import java.util.List;

public interface DestinationService {
    DestinationResponse addDestination(Long templateId, DestinationRequest request);
    DestinationResponse updateDestination(Long templateId, Long destinationId,
                                          DestinationRequest request);
    void deleteDestination(Long templateId, Long destinationId);
    List<DestinationResponse> findAllByTemplate(Long templateId);

    DestinationResponse addMapping(Long templateId, Long destinationId,
                                   DestinationMappingRequest request);
    DestinationResponse removeMapping(Long templateId, Long destinationId,
                                      Long mappingId);
}
