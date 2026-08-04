package com.xanxs.config_api.service;

import com.xanxs.config_api.dto.request.FieldTransformationRequest;
import com.xanxs.config_api.dto.request.TemplateFieldRequest;
import com.xanxs.config_api.dto.response.TemplateFieldResponse;

import java.util.List;

public interface TemplateFieldService {
    TemplateFieldResponse addField(Long templateId, TemplateFieldRequest request);
    TemplateFieldResponse updateField(Long templateId, Long fieldId, TemplateFieldRequest request);
    void deleteField(Long templateId, Long fieldId);
    List<TemplateFieldResponse> findAllByTemplate(Long templateId);

    TemplateFieldResponse addTransformation(Long templateId, Long fieldId,
                                            FieldTransformationRequest request);
    TemplateFieldResponse removeTransformation(Long templateId, Long fieldId,
                                               Long transformationId);
    TemplateFieldResponse reorderTransformations(Long templateId, Long fieldId,
                                                 List<Long> orderedIds);
}