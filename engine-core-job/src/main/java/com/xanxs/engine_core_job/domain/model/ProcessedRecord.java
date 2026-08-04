package com.xanxs.engine_core_job.domain.model;


import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ProcessedRecord {

    private Integer lineNumber;
    private String rawLine;

    // Resultado del procesamiento campo → valor transformado
    private Map<String, Object> fields;

    // Si hubo error en esta línea
    private boolean hasError;
    private String errorDescription;
    private String exceptionDetail;
}