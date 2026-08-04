package com.xanxs.config_api.controller;



import com.xanxs.config_api.dto.request.DestinationMappingRequest;
import com.xanxs.config_api.dto.request.DestinationRequest;
import com.xanxs.config_api.dto.response.DestinationResponse;
import com.xanxs.config_api.service.DestinationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/templates/{templateId}/destinations")
@RequiredArgsConstructor
public class DestinationController {

    private final DestinationService destinationService;

    @PostMapping
    public ResponseEntity<DestinationResponse> addDestination(
            @PathVariable Long templateId,
            @Valid @RequestBody DestinationRequest request) {
        log.debug("POST /api/v1/templates/{}/destinations - destino: {}",
                templateId, request.getName());
        DestinationResponse response = destinationService.addDestination(templateId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<DestinationResponse>> findAll(@PathVariable Long templateId) {
        log.debug("GET /api/v1/templates/{}/destinations", templateId);
        return ResponseEntity.ok(destinationService.findAllByTemplate(templateId));
    }

    @PutMapping("/{destinationId}")
    public ResponseEntity<DestinationResponse> updateDestination(
            @PathVariable Long templateId,
            @PathVariable Long destinationId,
            @Valid @RequestBody DestinationRequest request) {
        log.debug("PUT /api/v1/templates/{}/destinations/{}", templateId, destinationId);
        return ResponseEntity.ok(
                destinationService.updateDestination(templateId, destinationId, request));
    }

    @DeleteMapping("/{destinationId}")
    public ResponseEntity<Void> deleteDestination(
            @PathVariable Long templateId,
            @PathVariable Long destinationId) {
        log.debug("DELETE /api/v1/templates/{}/destinations/{}", templateId, destinationId);
        destinationService.deleteDestination(templateId, destinationId);
        return ResponseEntity.noContent().build();
    }

    // --- Mappings ---

    @PostMapping("/{destinationId}/mappings")
    public ResponseEntity<DestinationResponse> addMapping(
            @PathVariable Long templateId,
            @PathVariable Long destinationId,
            @Valid @RequestBody DestinationMappingRequest request) {
        log.debug("POST /api/v1/templates/{}/destinations/{}/mappings - campo: {}",
                templateId, destinationId, request.getFieldName());
        DestinationResponse response = destinationService.addMapping(
                templateId, destinationId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{destinationId}/mappings/{mappingId}")
    public ResponseEntity<DestinationResponse> removeMapping(
            @PathVariable Long templateId,
            @PathVariable Long destinationId,
            @PathVariable Long mappingId) {
        log.debug("DELETE /api/v1/templates/{}/destinations/{}/mappings/{}",
                templateId, destinationId, mappingId);
        DestinationResponse response = destinationService.removeMapping(
                templateId, destinationId, mappingId);
        return ResponseEntity.ok(response);
    }
}