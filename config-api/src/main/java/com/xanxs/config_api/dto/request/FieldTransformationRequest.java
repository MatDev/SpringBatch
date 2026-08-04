package com.xanxs.config_api.dto.request;



import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

@Data
public class FieldTransformationRequest {

    @NotBlank(message = "El nombre del transformer es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String transformerName;
    /*
        Valores: trim | uppercase | lowercase | parseDate |
        formatDate | replace | removeLeadingZeros |
        toInteger | toLong | formatRut | regexReplace
    */

    // Parámetros opcionales según el transformer
    private Map<String, String> parameters;

    // Si no viene se calcula automáticamente
    @Min(value = 0, message = "El orden debe ser mayor o igual a 0")
    private Integer orderIndex;
}