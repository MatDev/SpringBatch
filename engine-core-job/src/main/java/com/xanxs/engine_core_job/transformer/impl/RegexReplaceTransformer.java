package com.xanxs.engine_core_job.transformer.impl;

import com.xanxs.engine_core_job.transformer.api.FieldTransformer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RegexReplaceTransformer implements FieldTransformer {

    @Override
    public String getName() {
        return "regexReplace";
    }

    @Override
    public String transform(String value, Map<String, String> parameters) {
        if (value == null) return null;

        String pattern = parameters.getOrDefault("pattern", "");
        String replacement = parameters.getOrDefault("replacement", "");

        if (pattern.isBlank()) return value;

        try {
            return value.replaceAll(pattern, replacement);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("Patrón regex inválido: '%s'", pattern)
            );
        }
    }
}