package com.xanxs.config_api.dto.request;



import com.xanxs.config_api.domain.enums.DestinationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

@Data
public class DestinationRequest {

    @NotNull(message = "El tipo de destino es obligatorio")
    private DestinationType destinationType;

    @NotBlank(message = "El nombre del destino es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String name;

    // Campos según destinationType, se validan en el servicio
    private String targetTable;       // POSTGRESQL
    private String targetCollection;  // MONGODB
    private String targetTopic;       // KAFKA
    private String targetPath;        // FILE
    private String targetUrl;         // REST

    private Boolean active = true;

    private Map<String, Object> extraConfig;
}