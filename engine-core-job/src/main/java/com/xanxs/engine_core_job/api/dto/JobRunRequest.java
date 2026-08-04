package com.xanxs.engine_core_job.api.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JobRunRequest {

    @NotBlank(message = "El nombre del job es obligatorio")
    private String jobName;

    @NotNull(message = "El id de plantilla es obligatorio")
    private Long templateId;

    @NotBlank(message = "La ruta del archivo es obligatoria")
    private String filePath;
}