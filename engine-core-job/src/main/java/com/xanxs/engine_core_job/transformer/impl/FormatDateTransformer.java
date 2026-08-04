package com.xanxs.engine_core_job.transformer.impl;

import com.xanxs.engine_core_job.transformer.api.FieldTransformer;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class FormatDateTransformer implements FieldTransformer {

    @Override
    public String getName() {
        return "formatDate";
    }

    @Override
    public String transform(String value, Map<String, String> parameters) {
        if (value == null || value.isBlank()) return null;

        // Espera ISO date (yyyy-MM-dd) y lo formatea al outputFormat
        String outputFormat = parameters.getOrDefault("outputFormat", "dd/MM/yyyy");

        try {
            LocalDate date = LocalDate.parse(value.trim());
            return date.format(DateTimeFormatter.ofPattern(outputFormat));
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("No se puede formatear fecha '%s' a '%s'",
                            value, outputFormat)
            );
        }
    }
}