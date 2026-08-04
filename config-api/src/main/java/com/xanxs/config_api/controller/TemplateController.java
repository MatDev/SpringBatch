package com.xanxs.config_api.controller;


import com.xanxs.config_api.dto.request.TemplateRequest;
import com.xanxs.config_api.dto.response.TemplateResponse;
import com.xanxs.config_api.service.TemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateService templateService;

    @PostMapping
    public ResponseEntity<TemplateResponse> create(@Valid @RequestBody TemplateRequest request) {
        log.debug("POST /api/v1/templates - crear plantilla: {}", request.getName());
        TemplateResponse response = templateService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemplateResponse> findById(@PathVariable Long id) {
        log.debug("GET /api/v1/templates/{}", id);
        return ResponseEntity.ok(templateService.findById(id));
    }

    // Retorna plantilla completa con fields, transformaciones y destinos
    @GetMapping("/{id}/complete")
    public ResponseEntity<TemplateResponse> findByIdComplete(@PathVariable Long id) {
        log.debug("GET /api/v1/templates/{}/complete", id);
        return ResponseEntity.ok(templateService.findByIdComplete(id));
    }

    @GetMapping
    public ResponseEntity<List<TemplateResponse>> findAll(
            @RequestParam(required = false, defaultValue = "false") boolean onlyActive) {
        log.debug("GET /api/v1/templates - onlyActive: {}", onlyActive);
        List<TemplateResponse> response = onlyActive
                ? templateService.findAllActive()
                : templateService.findAll();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TemplateResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TemplateRequest request) {
        log.debug("PUT /api/v1/templates/{}", id);
        return ResponseEntity.ok(templateService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.debug("DELETE /api/v1/templates/{}", id);
        templateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}