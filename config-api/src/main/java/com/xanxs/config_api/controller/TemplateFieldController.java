package com.xanxs.config_api.controller;




import com.xanxs.config_api.dto.request.FieldTransformationRequest;
import com.xanxs.config_api.dto.request.TemplateFieldRequest;
import com.xanxs.config_api.dto.response.TemplateFieldResponse;
import com.xanxs.config_api.service.TemplateFieldService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/templates/{templateId}/fields")
@RequiredArgsConstructor
public class TemplateFieldController {

    private final TemplateFieldService fieldService;

    @PostMapping
    public ResponseEntity<TemplateFieldResponse> addField(
            @PathVariable Long templateId,
            @Valid @RequestBody TemplateFieldRequest request) {
        log.debug("POST /api/v1/templates/{}/fields - campo: {}", templateId, request.getName());
        TemplateFieldResponse response = fieldService.addField(templateId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TemplateFieldResponse>> findAll(@PathVariable Long templateId) {
        log.debug("GET /api/v1/templates/{}/fields", templateId);
        return ResponseEntity.ok(fieldService.findAllByTemplate(templateId));
    }

    @PutMapping("/{fieldId}")
    public ResponseEntity<TemplateFieldResponse> updateField(
            @PathVariable Long templateId,
            @PathVariable Long fieldId,
            @Valid @RequestBody TemplateFieldRequest request) {
        log.debug("PUT /api/v1/templates/{}/fields/{}", templateId, fieldId);
        return ResponseEntity.ok(fieldService.updateField(templateId, fieldId, request));
    }

    @DeleteMapping("/{fieldId}")
    public ResponseEntity<Void> deleteField(
            @PathVariable Long templateId,
            @PathVariable Long fieldId) {
        log.debug("DELETE /api/v1/templates/{}/fields/{}", templateId, fieldId);
        fieldService.deleteField(templateId, fieldId);
        return ResponseEntity.noContent().build();
    }

    // --- Transformaciones ---

    @PostMapping("/{fieldId}/transformations")
    public ResponseEntity<TemplateFieldResponse> addTransformation(
            @PathVariable Long templateId,
            @PathVariable Long fieldId,
            @Valid @RequestBody FieldTransformationRequest request) {
        log.debug("POST /api/v1/templates/{}/fields/{}/transformations - transformer: {}",
                templateId, fieldId, request.getTransformerName());
        TemplateFieldResponse response = fieldService.addTransformation(
                templateId, fieldId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{fieldId}/transformations/{transformationId}")
    public ResponseEntity<TemplateFieldResponse> removeTransformation(
            @PathVariable Long templateId,
            @PathVariable Long fieldId,
            @PathVariable Long transformationId) {
        log.debug("DELETE /api/v1/templates/{}/fields/{}/transformations/{}",
                templateId, fieldId, transformationId);
        TemplateFieldResponse response = fieldService.removeTransformation(
                templateId, fieldId, transformationId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{fieldId}/transformations/reorder")
    public ResponseEntity<TemplateFieldResponse> reorderTransformations(
            @PathVariable Long templateId,
            @PathVariable Long fieldId,
            @RequestBody List<Long> orderedIds) {
        log.debug("PATCH /api/v1/templates/{}/fields/{}/transformations/reorder",
                templateId, fieldId);
        TemplateFieldResponse response = fieldService.reorderTransformations(
                templateId, fieldId, orderedIds);
        return ResponseEntity.ok(response);
    }
}