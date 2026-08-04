package com.xanxs.engine_core.service;

// service/JobExecutionService.java

import com.xanxs.engine_core.dto.request.JobExecutionRequest;
import com.xanxs.engine_core.dto.response.JobExecutionErrorResponse;
import com.xanxs.engine_core.dto.response.JobExecutionResponse;
import com.xanxs.engine_core.dto.response.JobReportResponse;

import java.util.List;

public interface JobExecutionService {
    JobExecutionResponse execute(JobExecutionRequest request);
    JobExecutionResponse findById(Long id);
    JobExecutionResponse refreshStatus(Long id);   // sincroniza estado con el core
    List<JobExecutionResponse> findAll();
    List<JobExecutionResponse> findAllByStatus(String status);
    List<JobExecutionErrorResponse> findErrors(Long id);
    JobReportResponse getReport(Long id);
}