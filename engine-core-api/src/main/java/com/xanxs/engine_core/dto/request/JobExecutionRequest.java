package com.xanxs.engine_core.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JobExecutionRequest {

    @NotBlank(message = "El nombre del job es obligatorio")
    private String jobName;
    // Debe coincidir con un job registrado en el core

    @NotNull(message = "El id de plantilla es obligatorio")
    private Long templateId;

    @NotBlank(message = "La ruta del archivo es obligatoria")
    private String filePath;

    // Usuario o sistema que solicita la ejecución (opcional)
    private String requestedBy;
}