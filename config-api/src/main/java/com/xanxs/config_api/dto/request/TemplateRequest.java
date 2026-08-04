package com.xanxs.config_api.dto.request;

// dto/request/TemplateRequest.java



import com.xanxs.config_api.domain.enums.FileType;
import com.xanxs.config_api.domain.enums.OnErrorBehavior;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TemplateRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar 100 caracteres")
    private String name;

    @Size(max = 500, message = "La descripción no puede superar 500 caracteres")
    private String description;

    @NotNull(message = "El tipo de archivo es obligatorio")
    private FileType fileType;

    // Obligatorio solo si fileType = DELIMITED, se valida en el servicio
    private String separator;

    @NotBlank(message = "El encoding es obligatorio")
    private String encoding = "UTF-8";

    private Boolean skipEmptyLines = true;

    @NotNull(message = "El comportamiento ante errores es obligatorio")
    private OnErrorBehavior onError = OnErrorBehavior.CONTINUE;

    private Boolean active = true;
}
