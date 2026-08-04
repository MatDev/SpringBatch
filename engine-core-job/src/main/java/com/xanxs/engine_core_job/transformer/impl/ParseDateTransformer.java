package com.xanxs.engine_core_job.transformer.impl;



import com.xanxs.engine_core_job.transformer.api.FieldTransformer;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class ParseDateTransformer implements FieldTransformer {

    @Override
    public String getName() {
        return "parseDate";
    }

    @Override
    public String transform(String value, Map<String, String> parameters) {
        if (value == null || value.isBlank()) return null;

        String inputFormat = parameters.getOrDefault("inputFormat", "yyyyMMdd");
        String outputFormat = parameters.getOrDefault("outputFormat", "yyyy-MM-dd");

        try {
            DateTimeFormatter input = DateTimeFormatter.ofPattern(inputFormat);
            DateTimeFormatter output = DateTimeFormatter.ofPattern(outputFormat);
            LocalDate date = LocalDate.parse(value.trim(), input);
            return date.format(output);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("No se puede parsear '%s' con formato '%s'",
                            value, inputFormat)
            );
        }
    }
}