package com.xanxs.engine_core.controller;


import com.xanxs.engine_core.dto.request.JobExecutionRequest;
import com.xanxs.engine_core.dto.response.JobExecutionErrorResponse;
import com.xanxs.engine_core.dto.response.JobExecutionResponse;
import com.xanxs.engine_core.dto.response.JobReportResponse;
import com.xanxs.engine_core.service.JobExecutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
public class JobExecutionController {

    private final JobExecutionService jobExecutionService;

    @PostMapping("/execute")
    public ResponseEntity<JobExecutionResponse> execute(
            @Valid @RequestBody JobExecutionRequest request) {
        log.debug("POST /api/v1/jobs/execute - job: {}", request.getJobName());
        JobExecutionResponse response = jobExecutionService.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<JobExecutionResponse>> findAll(
            @RequestParam(required = false) String status) {
        log.debug("GET /api/v1/jobs - status: {}", status);
        List<JobExecutionResponse> response = status != null
                ? jobExecutionService.findAllByStatus(status)
                : jobExecutionService.findAll();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobExecutionResponse> findById(@PathVariable Long id) {
        log.debug("GET /api/v1/jobs/{}", id);
        return ResponseEntity.ok(jobExecutionService.findById(id));
    }

    // Sincroniza el estado de la ejecución consultando al core
    @PatchMapping("/{id}/refresh")
    public ResponseEntity<JobExecutionResponse> refreshStatus(@PathVariable Long id) {
        log.debug("PATCH /api/v1/jobs/{}/refresh", id);
        return ResponseEntity.ok(jobExecutionService.refreshStatus(id));
    }

    @GetMapping("/{id}/errors")
    public ResponseEntity<List<JobExecutionErrorResponse>> findErrors(@PathVariable Long id) {
        log.debug("GET /api/v1/jobs/{}/errors", id);
        return ResponseEntity.ok(jobExecutionService.findErrors(id));
    }

    @GetMapping("/{id}/report")
    public ResponseEntity<JobReportResponse> getReport(@PathVariable Long id) {
        log.debug("GET /api/v1/jobs/{}/report", id);
        return ResponseEntity.ok(jobExecutionService.getReport(id));
    }
}