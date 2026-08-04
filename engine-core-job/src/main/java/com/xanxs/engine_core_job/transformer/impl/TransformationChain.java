package com.xanxs.engine_core_job.transformer.impl;


import com.xanxs.engine_core_job.domain.model.TransformationConfig;
import com.xanxs.engine_core_job.transformer.api.FieldTransformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransformationChain {

    private final TransformerRegistry registry;

    // Aplica las transformaciones en orden sobre el valor
    public String apply(String value, List<TransformationConfig> transformations) {
        if (transformations == null || transformations.isEmpty()) return value;

        String current = value;

        for (TransformationConfig config : transformations) {
            try {
                FieldTransformer transformer = registry.get(config.getTransformerName());
                String previous = current;
                current = transformer.transform(current, config.getParameters());

                log.debug("Transformer '{}': '{}' → '{}'",
                        config.getTransformerName(), previous, current);

            } catch (IllegalArgumentException e) {
                // Error de transformación — relanzar con contexto
                throw new IllegalArgumentException(
                        String.format("Error en transformer '%s': %s",
                                config.getTransformerName(), e.getMessage())
                );
            }
        }

        return current;
    }
}