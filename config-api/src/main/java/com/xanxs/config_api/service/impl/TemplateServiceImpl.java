package com.xanxs.config_api.service.impl;


import com.xanxs.config_api.domain.entity.Template;
import com.xanxs.config_api.dto.request.TemplateRequest;
import com.xanxs.config_api.dto.response.TemplateResponse;
import com.xanxs.config_api.exception.BusinessException;
import com.xanxs.config_api.exception.ResourceNotFoundException;
import com.xanxs.config_api.mapper.TemplateMapper;
import com.xanxs.config_api.repository.TemplateRepository;
import com.xanxs.config_api.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    private final TemplateRepository templateRepository;
    private final TemplateMapper templateMapper;

    @Override
    @Transactional
    public TemplateResponse create(TemplateRequest request) {
        log.debug("Creando plantilla: {}", request.getName());

        // Validar nombre único
        if (templateRepository.existsByName(request.getName())) {
            throw new BusinessException(
                    String.format("Ya existe una plantilla con el nombre '%s'", request.getName())
            );
        }

        // Validar separador si es DELIMITED
        validateSeparator(request);

        Template template = templateMapper.toEntity(request);
        Template saved = templateRepository.save(template);

        log.info("Plantilla creada con id: {}", saved.getId());
        return templateMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TemplateResponse findById(Long id) {
        Template template = getTemplateOrThrow(id);
        return templateMapper.toResponse(template);
    }

    @Override
    @Transactional(readOnly = true)
    public TemplateResponse findByIdComplete(Long id) {
        // Carga fields + transformaciones
        Template template = templateRepository
                .findByIdWithFieldsAndTransformations(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template", id));

        // Segunda query para destinations + mappings
        // Evitamos MultipleBagFetchException con 2 queries separadas
        templateRepository.findByIdWithDestinations(id)
                .ifPresent(t -> template.getDestinations()
                        .addAll(t.getDestinations()));

        return templateMapper.toResponse(template);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TemplateResponse> findAll() {
        return templateRepository.findAll()
                .stream()
                .map(templateMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TemplateResponse> findAllActive() {
        return templateRepository.findAllByActiveTrue()
                .stream()
                .map(templateMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TemplateResponse update(Long id, TemplateRequest request) {
        log.debug("Actualizando plantilla id: {}", id);

        Template template = getTemplateOrThrow(id);

        // Validar nombre único solo si cambió
        if (!template.getName().equals(request.getName())
                && templateRepository.existsByName(request.getName())) {
            throw new BusinessException(
                    String.format("Ya existe una plantilla con el nombre '%s'", request.getName())
            );
        }

        validateSeparator(request);

        templateMapper.updateEntity(template, request);
        Template updated = templateRepository.save(template);

        log.info("Plantilla actualizada id: {}", updated.getId());
        return templateMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.debug("Eliminando plantilla id: {}", id);
        Template template = getTemplateOrThrow(id);
        templateRepository.delete(template);
        log.info("Plantilla eliminada id: {}", id);
    }

    // --- Helpers privados ---

    private Template getTemplateOrThrow(Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Template", id));
    }

    private void validateSeparator(TemplateRequest request) {
        if (request.getFileType() == null) return;

        switch (request.getFileType()) {
            case DELIMITED -> {
                if (request.getSeparator() == null || request.getSeparator().isBlank()) {
                    throw new BusinessException(
                            "El separador es obligatorio para archivos de tipo DELIMITED"
                    );
                }
            }
            case FIXED -> {
                if (request.getSeparator() != null && !request.getSeparator().isBlank()) {
                    throw new BusinessException(
                            "Los archivos de tipo FIXED no deben tener separador"
                    );
                }
            }
        }
    }
}