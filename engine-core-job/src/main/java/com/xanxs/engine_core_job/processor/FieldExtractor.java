package com.xanxs.engine_core_job.processor;

// processor/FieldExtractor.java
import com.xanxs.engine_core_job.domain.model.FieldConfig;
import com.xanxs.engine_core_job.domain.model.TemplateConfig;
import com.xanxs.engine_core_job.reader.TemplateAwareReader;
import com.xanxs.engine_core_job.transformer.impl.TransformationChain;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class FieldExtractor {

    private final TransformationChain transformationChain;

    // Extrae y transforma todos los campos de una línea
    public Map<String, Object> extract(String line,
                                       TemplateConfig template,
                                       TemplateAwareReader reader) {
        // 1. Extraer campos raw
        Map<String, String> rawFields = reader.mapLine(line);
        Map<String, Object> result = new LinkedHashMap<>();

        // 2. Por cada campo aplicar transformaciones y castear tipo
        for (FieldConfig field : template.getFields()) {
            String rawValue = rawFields.get(field.getName());

            // Aplicar valor por defecto si viene nulo o vacío
            if ((rawValue == null || rawValue.isBlank())
                    && field.getDefaultValue() != null) {
                rawValue = field.getDefaultValue();
            }

            // Aplicar cadena de transformaciones
            String transformed = transformationChain.apply(
                    rawValue, field.getTransformations());

            // Castear al tipo definido
            Object typed = castToType(transformed, field.getFieldType());
            result.put(field.getName(), typed);
        }

        return result;
    }

    private Object castToType(String value, String fieldType) {
        if (value == null) return null;
        return switch (fieldType) {
            case "INTEGER" -> {
                try { yield Integer.parseInt(value.trim()); }
                catch (NumberFormatException e) { yield value; }
            }
            case "LONG" -> {
                try { yield Long.parseLong(value.trim()); }
                catch (NumberFormatException e) { yield value; }
            }
            case "DECIMAL" -> {
                try { yield Double.parseDouble(value.trim().replace(",", ".")); }
                catch (NumberFormatException e) { yield value; }
            }
            case "BOOLEAN" -> {
                String lower = value.trim().toLowerCase();
                yield lower.equals("true") || lower.equals("1");
            }
            default -> value; // STRING y DATE se mantienen como String
        };
    }
}