package com.xanxs.engine_core_job.transformer.impl;

import com.xanxs.engine_core_job.transformer.api.FieldTransformer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FormatRutTransformer implements FieldTransformer {

    @Override
    public String getName() {
        return "formatRut";
    }

    @Override
    public String transform(String value, Map<String, String> parameters) {
        if (value == null || value.isBlank()) return null;

        // Limpiar el valor — quitar puntos y guión
        String clean = value.trim()
                .replace(".", "")
                .replace("-", "")
                .toUpperCase();

        if (clean.length() < 2) {
            throw new IllegalArgumentException(
                    String.format("RUT inválido: '%s'", value)
            );
        }

        // Separar cuerpo y dígito verificador
        String body = clean.substring(0, clean.length() - 1);
        String dv = clean.substring(clean.length() - 1);

        // Formatear con puntos
        StringBuilder formatted = new StringBuilder();
        int count = 0;
        for (int i = body.length() - 1; i >= 0; i--) {
            formatted.insert(0, body.charAt(i));
            count++;
            if (count % 3 == 0 && i != 0) {
                formatted.insert(0, '.');
            }
        }

        String withFormat = parameters.getOrDefault("format", "POINTS_DASH");

        return switch (withFormat) {
            case "CLEAN"       -> body + dv;              // Sin formato
            case "DASH_ONLY"   -> body + "-" + dv;        // Solo guión
            default            -> formatted + "-" + dv;   // Con puntos y guión
        };
    }
}