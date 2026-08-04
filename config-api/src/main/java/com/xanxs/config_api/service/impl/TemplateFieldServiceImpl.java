package com.xanxs.config_api.service.impl;


import com.xanxs.config_api.domain.entity.FieldTransformation;
import com.xanxs.config_api.domain.entity.Template;
import com.xanxs.config_api.domain.entity.TemplateField;
import com.xanxs.config_api.domain.enums.FileType;
import com.xanxs.config_api.dto.request.FieldTransformationRequest;
import com.xanxs.config_api.dto.request.TemplateFieldRequest;
import com.xanxs.config_api.dto.response.TemplateFieldResponse;
import com.xanxs.config_api.exception.BusinessException;
import com.xanxs.config_api.exception.ResourceNotFoundException;
import com.xanxs.config_api.mapper.FieldTransformationMapper;
import com.xanxs.config_api.mapper.TemplateFieldMapper;
import com.xanxs.config_api.repository.FieldTransformationRepository;
import com.xanxs.config_api.repository.TemplateFieldRepository;
import com.xanxs.config_api.repository.TemplateRepository;
import com.xanxs.config_api.service.TemplateFieldService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateFieldServiceImpl implements TemplateFieldService {

    private final TemplateRepository templateRepository;
    private final TemplateFieldRepository fieldRepository;
    private final FieldTransformationRepository transformationRepository;
    private final TemplateFieldMapper fieldMapper;
    private final FieldTransformationMapper transformationMapper;

    @Override
    @Transactional
    public TemplateFieldResponse addField(Long templateId, TemplateFieldRequest request) {
        log.debug("Agregando campo '{}' a plantilla id: {}", request.getName(), templateId);

        Template template = getTemplateOrThrow(templateId);

        // Validar nombre único dentro de la plantilla
        if (fieldRepository.existsByTemplateIdAndName(templateId, request.getName())) {
            throw new BusinessException(
                    String.format("Ya existe un campo con el nombre '%s' en esta plantilla",
                            request.getName())
            );
        }

        // Validar longitud si es FIXED
        validateFieldForFileType(template.getFileType(), request);

        // Calcular orderIndex automáticamente si no viene
        if (request.getOrderIndex() == null) {
            Integer maxOrder = fieldRepository.findMaxOrderIndexByTemplateId(templateId);
            request.setOrderIndex(maxOrder + 1);
        }

        TemplateField field = fieldMapper.toEntity(request);
        field.setTemplate(template);

        TemplateField saved = fieldRepository.save(field);
        log.info("Campo '{}' agregado con id: {}", saved.getName(), saved.getId());

        return fieldMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public TemplateFieldResponse updateField(Long templateId, Long fieldId,
                                             TemplateFieldRequest request) {
        log.debug("Actualizando campo id: {} de plantilla id: {}", fieldId, templateId);

        Template template = getTemplateOrThrow(templateId);
        TemplateField field = getFieldOrThrow(fieldId, templateId);

        // Validar nombre único solo si cambió
        if (!field.getName().equals(request.getName())
                && fieldRepository.existsByTemplateIdAndName(templateId, request.getName())) {
            throw new BusinessException(
                    String.format("Ya existe un campo con el nombre '%s' en esta plantilla",
                            request.getName())
            );
        }

        validateFieldForFileType(template.getFileType(), request);

        fieldMapper.updateEntity(field, request);
        TemplateField updated = fieldRepository.save(field);

        log.info("Campo id: {} actualizado", updated.getId());
        return fieldMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteField(Long templateId, Long fieldId) {
        log.debug("Eliminando campo id: {} de plantilla id: {}", fieldId, templateId);
        getTemplateOrThrow(templateId);
        TemplateField field = getFieldOrThrow(fieldId, templateId);
        fieldRepository.delete(field);
        log.info("Campo id: {} eliminado", fieldId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TemplateFieldResponse> findAllByTemplate(Long templateId) {
        getTemplateOrThrow(templateId);
        return fieldRepository
                .findAllByTemplateIdOrderByOrderIndexAsc(templateId)
                .stream()
                .map(fieldMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public TemplateFieldResponse addTransformation(Long templateId, Long fieldId,
                                                   FieldTransformationRequest request) {
        log.debug("Agregando transformación '{}' al campo id: {}",
                request.getTransformerName(), fieldId);

        getTemplateOrThrow(templateId);
        TemplateField field = fieldRepository.findByIdWithTransformations(fieldId)
                .orElseThrow(() -> new ResourceNotFoundException("TemplateField", fieldId));

        // Calcular orderIndex automáticamente si no viene
        if (request.getOrderIndex() == null) {
            Integer maxOrder = transformationRepository.findMaxOrderIndexByFieldId(fieldId);
            request.setOrderIndex(maxOrder + 1);
        }

        // Validar que no exista el mismo orderIndex
        if (transformationRepository.existsByFieldIdAndOrderIndex(
                fieldId, request.getOrderIndex())) {
            throw new BusinessException(
                    String.format("Ya existe una transformación en la posición %d para este campo",
                            request.getOrderIndex())
            );
        }

        FieldTransformation transformation = transformationMapper.toEntity(request);
        field.addTransformation(transformation);

        TemplateField saved = fieldRepository.save(field);
        log.info("Transformación '{}' agregada al campo id: {}",
                request.getTransformerName(), fieldId);

        return fieldMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public TemplateFieldResponse removeTransformation(Long templateId, Long fieldId,
                                                      Long transformationId) {
        log.debug("Eliminando transformación id: {} del campo id: {}", transformationId, fieldId);

        getTemplateOrThrow(templateId);
        TemplateField field = fieldRepository.findByIdWithTransformations(fieldId)
                .orElseThrow(() -> new ResourceNotFoundException("TemplateField", fieldId));

        FieldTransformation transformation = field.getTransformations().stream()
                .filter(t -> t.getId().equals(transformationId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Transformación id %d no encontrada en campo id %d",
                                transformationId, fieldId))
                );

        field.removeTransformation(transformation);
        TemplateField saved = fieldRepository.save(field);

        log.info("Transformación id: {} eliminada", transformationId);
        return fieldMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public TemplateFieldResponse reorderTransformations(Long templateId, Long fieldId,
                                                        List<Long> orderedIds) {
        log.debug("Reordenando transformaciones del campo id: {}", fieldId);

        getTemplateOrThrow(templateId);
        TemplateField field = fieldRepository.findByIdWithTransformations(fieldId)
                .orElseThrow(() -> new ResourceNotFoundException("TemplateField", fieldId));

        // Validar que los ids correspondan al campo
        List<Long> existingIds = field.getTransformations()
                .stream()
                .map(FieldTransformation::getId)
                .toList();

        boolean allMatch = orderedIds.containsAll(existingIds)
                && existingIds.containsAll(orderedIds);

        if (!allMatch) {
            throw new BusinessException(
                    "Los ids proporcionados no coinciden con las transformaciones del campo"
            );
        }

        // Reasignar orderIndex según la lista recibida
        AtomicInteger order = new AtomicInteger(1);
        orderedIds.forEach(tid ->
                field.getTransformations().stream()
                        .filter(t -> t.getId().equals(tid))
                        .findFirst()
                        .ifPresent(t -> t.setOrderIndex(order.getAndIncrement()))
        );

        TemplateField saved = fieldRepository.save(field);
        log.info("Transformaciones reordenadas para campo id: {}", fieldId);

        return fieldMapper.toResponse(saved);
    }

    // --- Helpers privados ---

    private Template getTemplateOrThrow(Long templateId) {
        return templateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Template", templateId));
    }

    private TemplateField getFieldOrThrow(Long fieldId, Long templateId) {
        return fieldRepository.findById(fieldId)
                .filter(f -> f.getTemplate().getId().equals(templateId))
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format("Campo id %d no encontrado en plantilla id %d",
                                fieldId, templateId))
                );
    }

    private void validateFieldForFileType(FileType fileType, TemplateFieldRequest request) {
        if (fileType == FileType.FIXED) {
            if (request.getLength() == null || request.getLength() <= 0) {
                throw new BusinessException(
                        "La longitud es obligatoria para campos de archivos de tipo FIXED"
                );
            }
        }
    }
}