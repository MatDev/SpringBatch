package com.xanxs.engine_core_job.processor;

import com.xanxs.engine_core_job.domain.model.FieldConfig;
import com.xanxs.engine_core_job.domain.model.ProcessedRecord;
import com.xanxs.engine_core_job.domain.model.TemplateConfig;
import com.xanxs.engine_core_job.processor.validator.RecordValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class DefaultRecordValidator implements RecordValidator {

    @Override
    public void validate(ProcessedRecord record, TemplateConfig template) {
        List<String> errors = new ArrayList<>();

        for (FieldConfig field : template.getFields()) {
            Object value = record.getFields().get(field.getName());
            String strValue = value != null ? value.toString() : null;

            // Validar obligatorio
            if (Boolean.TRUE.equals(field.getRequired())) {
                if (strValue == null || strValue.isBlank()) {
                    errors.add(String.format(
                            "Campo obligatorio '%s' está vacío", field.getName()));
                    continue;
                }
            }

            // Si el valor es nulo y no es obligatorio, skip
            if (strValue == null || strValue.isBlank()) continue;

            // Validar tipo
            validateType(field, strValue, errors);
        }

        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }
    }

    private void validateType(FieldConfig field, String value, List<String> errors) {
        switch (field.getFieldType()) {
            case "INTEGER" -> {
                try {
                    Integer.parseInt(value.trim());
                } catch (NumberFormatException e) {
                    errors.add(String.format(
                            "Campo '%s' debe ser INTEGER, valor: '%s'",
                            field.getName(), value));
                }
            }
            case "LONG" -> {
                try {
                    Long.parseLong(value.trim());
                } catch (NumberFormatException e) {
                    errors.add(String.format(
                            "Campo '%s' debe ser LONG, valor: '%s'",
                            field.getName(), value));
                }
            }
            case "DECIMAL" -> {
                try {
                    Double.parseDouble(value.trim().replace(",", "."));
                } catch (NumberFormatException e) {
                    errors.add(String.format(
                            "Campo '%s' debe ser DECIMAL, valor: '%s'",
                            field.getName(), value));
                }
            }
            case "DATE" -> {
                try {
                    LocalDate.parse(value.trim());
                } catch (Exception e) {
                    errors.add(String.format(
                            "Campo '%s' debe ser DATE (yyyy-MM-dd), valor: '%s'",
                            field.getName(), value));
                }
            }
            case "BOOLEAN" -> {
                String lower = value.trim().toLowerCase();
                if (!lower.equals("true") && !lower.equals("false")
                        && !lower.equals("1") && !lower.equals("0")) {
                    errors.add(String.format(
                            "Campo '%s' debe ser BOOLEAN, valor: '%s'",
                            field.getName(), value));
                }
            }
            // STRING no necesita validación de tipo
        }
    }
}