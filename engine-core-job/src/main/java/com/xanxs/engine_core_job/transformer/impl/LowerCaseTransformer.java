package com.xanxs.engine_core_job.transformer.impl;

// transformer/impl/LowerCaseTransformer.java
import com.xanxs.engine_core_job.transformer.api.FieldTransformer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LowerCaseTransformer implements FieldTransformer {

    @Override
    public String getName() {
        return "lowercase";
    }

    @Override
    public String transform(String value, Map<String, String> parameters) {
        if (value == null) return null;
        return value.toLowerCase();
    }
}