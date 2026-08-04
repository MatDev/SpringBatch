package com.xanxs.config_api.dto.response;


import com.xanxs.config_api.domain.enums.DestinationType;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class DestinationResponse {
    private Long id;
    private DestinationType destinationType;
    private String name;
    private String targetTable;
    private String targetCollection;
    private String targetTopic;
    private String targetPath;
    private String targetUrl;
    private Boolean active;
    private Map<String, Object> extraConfig;
    private List<DestinationMappingResponse> mappings;
}