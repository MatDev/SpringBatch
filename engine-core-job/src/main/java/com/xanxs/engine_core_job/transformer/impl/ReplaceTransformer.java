package com.xanxs.engine_core_job.transformer.impl;

import com.xanxs.engine_core_job.transformer.api.FieldTransformer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ReplaceTransformer implements FieldTransformer {

    @Override
    public String getName() {
        return "replace";
    }

    @Override
    public String transform(String value, Map<String, String> parameters) {
        if (value == null) return null;

        String target = parameters.getOrDefault("target", "");
        String replacement = parameters.getOrDefault("replacement", "");

        return value.replace(target, replacement);
    }
}