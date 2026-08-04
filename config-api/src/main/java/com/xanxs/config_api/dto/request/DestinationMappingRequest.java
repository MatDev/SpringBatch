package com.xanxs.config_api.dto.request;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DestinationMappingRequest {

    @NotBlank(message = "El nombre del campo origen es obligatorio")
    @Size(max = 100)
    private String fieldName;

    @NotBlank(message = "El nombre de la columna destino es obligatorio")
    @Size(max = 100)
    private String targetColumn;

    private Integer orderIndex;
}