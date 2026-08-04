package com.xanxs.engine_core_job.transformer.impl;

import com.xanxs.engine_core_job.transformer.api.FieldTransformer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ToLongTransformer implements FieldTransformer {

    @Override
    public String getName() {
        return "toLong";
    }

    @Override
    public String transform(String value, Map<String, String> parameters) {
        if (value == null || value.isBlank()) return null;
        try {
            return String.valueOf(Long.parseLong(value.trim()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("No se puede convertir '%s' a Long", value)
            );
        }
    }
}