package com.xanxs.engine_core_job.reader;

import com.xanxs.engine_core_job.domain.model.FieldConfig;
import com.xanxs.engine_core_job.domain.model.TemplateConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class DelimitedLineMapper {

    private final TemplateConfig template;

    public Map<String, String> map(String line) {
        Map<String, String> fields = new LinkedHashMap<>();

        // Resolver el separador
        String separator = resolveSeparator(template.getSeparator());
        String[] parts = line.split(separator, -1);
        // -1 para preservar campos vacíos al final

        for (FieldConfig field : template.getFields()) {
            int index = field.getPosition();
            String value;

            if (index >= parts.length) {
                value = field.getDefaultValue();
                log.debug("Campo '{}' en posición {} no existe en línea con {} columnas",
                        field.getName(), index, parts.length);
            } else {
                value = parts[index];
            }

            fields.put(field.getName(), value);
        }

        return fields;
    }

    private String resolveSeparator(String separator) {
        if (separator == null) return "\\|";
        return switch (separator) {
            case "|"    -> "\\|";   // pipe necesita escape en regex
            case "\t"   -> "\\t";
            case "TAB"  -> "\\t";
            default     -> separator;
        };
    }
}