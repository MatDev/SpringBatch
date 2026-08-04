package com.xanxs.engine_core_job.transformer.impl;

import com.xanxs.engine_core_job.transformer.api.FieldTransformer;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Map;

// transformer/impl/Base64Transformer.java
@Component
public class Base64Transformer implements FieldTransformer {

    @Override
    public String getName() {
        return "base64encode";
    }

    @Override
    public String transform(String value, Map<String, String> parameters) {
        if (value == null) return null;
        return Base64.getEncoder().encodeToString(value.getBytes());
    }
}
// El TransformerRegistry lo detecta automáticamente al iniciar