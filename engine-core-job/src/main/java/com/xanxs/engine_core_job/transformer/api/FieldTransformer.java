package com.xanxs.engine_core_job.transformer.api;


import java.util.Map;

public interface FieldTransformer {

    // Nombre con el que se registra en la plantilla
    String getName();

    // Aplica la transformación al valor recibido
    String transform(String value, Map<String, String> parameters);
}