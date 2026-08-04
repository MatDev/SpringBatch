package com.xanxs.engine_core_job.reader;

import com.xanxs.engine_core_job.domain.model.FieldConfig;
import com.xanxs.engine_core_job.domain.model.TemplateConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class FixedLengthLineMapper {

    private final TemplateConfig template;

    // Extrae campos de una línea de longitud fija
    public Map<String, String> map(String line) {
        Map<String, String> fields = new LinkedHashMap<>();

        for (FieldConfig field : template.getFields()) {
            int start = field.getPosition();
            int end = start + field.getLength();

            String value;

            if (start >= line.length()) {
                // La línea es más corta de lo esperado
                value = field.getDefaultValue();
                log.debug("Campo '{}' fuera de rango en línea de longitud {}",
                        field.getName(), line.length());
            } else {
                // Recortar si el fin supera la longitud de la línea
                int safeEnd = Math.min(end, line.length());
                value = line.substring(start, safeEnd);
            }

            fields.put(field.getName(), value);
        }

        return fields;
    }
}