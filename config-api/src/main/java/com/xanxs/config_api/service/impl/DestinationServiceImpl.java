package com.xanxs.config_api.service.impl;


import com.xanxs.config_api.domain.entity.Destination;
import com.xanxs.config_api.domain.entity.DestinationMapping;
import com.xanxs.config_api.domain.entity.Template;
import com.xanxs.config_api.dto.request.DestinationMappingRequest;
import com.xanxs.config_api.dto.request.DestinationRequest;
import com.xanxs.config_api.dto.response.DestinationResponse;
import com.xanxs.config_api.exception.BusinessException;
import com.xanxs.config_api.exception.ResourceNotFoundException;
import com.xanxs.config_api.mapper.DestinationMapper;
import com.xanxs.config_api.repository.DestinationRepository;
import com.xanxs.config_api.repository.TemplateRepository;
import com.xanxs.config_api.service.DestinationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DestinationServiceImpl implements DestinationService {

    private final TemplateRepository templateRepository;
    private final DestinationRepository destinationRepository;
    private final DestinationMapper destinationMapper;

    @Override
    @Transactional
    public DestinationResponse addDestination(Long templateId, DestinationRequest request) {
        log.debug("Agregando destino '{}' a plantilla id: {}", request.getName(), templateId);

        Template template = getTemplateOrThrow(templateId);

        validateDestinationTarget(request);

        Destination destination = destinationMapper.toEntity(request);
        destination.setTemplate(template);

        Destination saved = destinationRepository.save(destination);
        log.info("Destino '{}' agregado con id: {}", saved.getName(), saved.getId());

        return destinationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public DestinationResponse updateDestination(Long templateId, Long destinationId,
                                                 DestinationRequest request) {
        log.debug("Actualizando destino id: {} de plantilla id: {}", destinationId, templateId);

        getTemplateOrThrow(templateId);
        Destination destination = getDestinationOrThrow(destinationId, templateId);

        validateDestinationTarget(request);

        destinationMapper.updateEntity(destination, request);
        Destination updated = destinationRepository.save(destination);

        log.info("Destino id: {} actualizado", updated.getId());
        return destinationMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteDestination(Long templateId, Long destinationId) {
        log.debug("Eliminando destino id: {} de plantilla id: {}", destinationId, templateId);
        getTemplateOrThrow(templateId);
        Destination destination = getDestinationOrThrow(destinationId, templateId);
        destinationRepository.delete(destination);
        log.info("Destino id: {} eliminado", destinationId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DestinationResponse> findAllByTemplate(Long templateId) {
        getTemplateOrThrow(templateId);
        return destinationRepository.findAllByTemplateId(templateId)
                .stream()
                .map(destinationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public DestinationResponse addMapping(Long templateId, Long destinationId,
                                          DestinationMappingRequest request) {
        log.debug("Agregando mapping '{}' a destino id: {}", request.getFieldName(), destinationId);

        getTemplateOrThrow(templateId);
        Destination destination = destinationRepository.findByIdWithMappings(destinationId)
                .orElseThrow(() -> new ResourceNotFoundException("Destination", destinationId));

        // Validar que no exista el mismo campo mapeado
        if (destination.getMappings().stream()
                .anyMatch(m -> m.getFieldName().equals(request.getFieldName()))) {
            throw new BusinessException(
                    String.format("El campo '%s' ya tiene un mapeo en este destino",
                            request.getFieldName())
            );
        }

        // Calcular orderIndex si no viene
        if (request.getOrderIndex() == null) {
            request.setOrderIndex(destination.getMappings().size() + 1);
        }

        DestinationMapping mapping = destinationMapper.toMappingEntity(request);
        destination.addMapping(mapping);

        Destination saved = destinationRepository.save(destination);
        log.info("Mapping '{}' agregado a destino id: {}", request.getFieldName(), destinationId);

        return destinationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public DestinationResponse removeMapping(Long templateId, Long destinationId,
                                             Long mappingId) {
        log.debug("Eliminando mapping id: {} de destino id: {}", mappingId, destinationId);

        getTemplateOrThrow(templateId);
        Destination destination = destinationRepository.findByIdWithMappings(destinationId)
                .orElseThrow(() -> new ResourceNotFoundException("Destination", destinationId));

        DestinationMapping mapping = destination.getMappings().stream()
                .filter(m -> m.getId().equals(mappingId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Mapping id %d no encontrado en destino id %d",
                                mappingId, destinationId))
                );

        destination.removeMapping(mapping);
        Destination saved = destinationRepository.save(destination);

        log.info("Mapping id: {} eliminado", mappingId);
        return destinationMapper.toResponse(saved);
    }

    // --- Helpers privados ---

    private Template getTemplateOrThrow(Long templateId) {
        return templateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Template", templateId));
    }

    private Destination getDestinationOrThrow(Long destinationId, Long templateId) {
        return destinationRepository.findById(destinationId)
                .filter(d -> d.getTemplate().getId().equals(templateId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Destino id %d no encontrado en plantilla id %d",
                                destinationId, templateId))
                );
    }

    private void validateDestinationTarget(DestinationRequest request) {
        switch (request.getDestinationType()) {
            case POSTGRESQL -> {
                if (request.getTargetTable() == null || request.getTargetTable().isBlank()) {
                    throw new BusinessException(
                            "La tabla destino es obligatoria para destinos de tipo POSTGRESQL"
                    );
                }
            }
            case MONGODB -> {
                if (request.getTargetCollection() == null
                        || request.getTargetCollection().isBlank()) {
                    throw new BusinessException(
                            "La colección destino es obligatoria para destinos de tipo MONGODB"
                    );
                }
            }
            case KAFKA -> {
                if (request.getTargetTopic() == null || request.getTargetTopic().isBlank()) {
                    throw new BusinessException(
                            "El topic es obligatorio para destinos de tipo KAFKA"
                    );
                }
            }
            case FILE -> {
                if (request.getTargetPath() == null || request.getTargetPath().isBlank()) {
                    throw new BusinessException(
                            "La ruta es obligatoria para destinos de tipo FILE"
                    );
                }
            }
            case REST -> {
                if (request.getTargetUrl() == null || request.getTargetUrl().isBlank()) {
                    throw new BusinessException(
                            "La URL es obligatoria para destinos de tipo REST"
                    );
                }
            }
        }
    }
}