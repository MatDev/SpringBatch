package com.xanxs.engine_core_job.transformer.impl;

import com.xanxs.engine_core_job.transformer.api.FieldTransformer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TrimTransformer implements FieldTransformer {

    @Override
    public String getName() {
        return "trim";
    }

    @Override
    public String transform(String value, Map<String, String> parameters) {
        if (value == null) return null;
        return value.trim();
    }
}