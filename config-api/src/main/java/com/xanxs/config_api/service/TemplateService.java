package com.xanxs.config_api.service;

import com.xanxs.config_api.dto.request.TemplateRequest;
import com.xanxs.config_api.dto.response.TemplateResponse;

import java.util.List;

public interface TemplateService {
    TemplateResponse create(TemplateRequest request);
    TemplateResponse findById(Long id);
    TemplateResponse findByIdComplete(Long id);  // con fields y destinations
    List<TemplateResponse> findAll();
    List<TemplateResponse> findAllActive();
    TemplateResponse update(Long id, TemplateRequest request);
    void delete(Long id);
}