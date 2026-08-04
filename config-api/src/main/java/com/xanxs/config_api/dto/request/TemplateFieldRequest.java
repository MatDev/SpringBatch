package com.xanxs.config_api.dto.request;

// dto/request/TemplateFieldRequest.java

import com.xanxs.config_api.domain.enums.FieldType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TemplateFieldRequest {

    @NotBlank(message = "El nombre del campo es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String name;

    @NotNull(message = "El tipo de campo es obligatorio")
    private FieldType fieldType;

    @NotNull(message = "La posición es obligatoria")
    @Min(value = 0, message = "La posición debe ser mayor o igual a 0")
    private Integer position;

    // Solo para FIXED, se valida en el servicio
    private Integer length;

    private Boolean required = false;

    @Size(max = 255, message = "El valor por defecto no puede superar 255 caracteres")
    private String defaultValue;

    // Si no viene se calcula automáticamente
    private Integer orderIndex;
}