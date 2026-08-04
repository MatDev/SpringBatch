package com.xanxs.config_api.dto.response;


import com.xanxs.config_api.domain.enums.FileType;
import com.xanxs.config_api.domain.enums.OnErrorBehavior;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TemplateResponse {
    private Long id;
    private String name;
    private String description;
    private FileType fileType;
    private String separator;
    private String encoding;
    private Boolean skipEmptyLines;
    private OnErrorBehavior onError;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TemplateFieldResponse> fields;
    private List<DestinationResponse> destinations;
}