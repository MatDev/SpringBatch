package com.xanxs.engine_core_job.transformer.impl;

// transformer/impl/RemoveLeadingZerosTransformer.java
import com.xanxs.engine_core_job.transformer.api.FieldTransformer;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RemoveLeadingZerosTransformer implements FieldTransformer {

    @Override
    public String getName() {
        return "removeLeadingZeros";
    }

    @Override
    public String transform(String value, Map<String, String> parameters) {
        if (value == null || value.isBlank()) return value;
        // Elimina ceros a la izquierda preservando al menos un dígito
        String result = value.replaceAll("^0+(?!$)", "");
        return result.isEmpty() ? "0" : result;
    }
}