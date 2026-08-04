package com.xanxs.engine_core_job.transformer.impl;

import com.xanxs.engine_core_job.transformer.api.FieldTransformer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ToIntegerTransformer implements FieldTransformer {

    @Override
    public String getName() {
        return "toInteger";
    }

    @Override
    public String transform(String value, Map<String, String> parameters) {
        if (value == null || value.isBlank()) return null;
        try {
            return String.valueOf(Integer.parseInt(value.trim()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("No se puede convertir '%s' a Integer", value)
            );
        }
    }
}