package com.xanxs.engine_core_job.transformer.impl;

import com.xanxs.engine_core_job.transformer.api.FieldTransformer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransformerRegistry {

    // Spring inyecta todos los beans que implementan FieldTransformer
    private final List<FieldTransformer> transformers;

    private Map<String, FieldTransformer> registry;

    @PostConstruct
    public void init() {
        registry = transformers.stream()
                .collect(Collectors.toMap(
                        FieldTransformer::getName,
                        Function.identity()
                ));

        log.info("TransformerRegistry inicializado con {} transformers: {}",
                registry.size(), registry.keySet());
    }

    public FieldTransformer get(String name) {
        FieldTransformer transformer = registry.get(name);
        if (transformer == null) {
            throw new IllegalArgumentException(
                    String.format("Transformer '%s' no encontrado. " +
                            "Disponibles: %s", name, registry.keySet())
            );
        }
        return transformer;
    }

    public boolean exists(String name) {
        return registry.containsKey(name);
    }

    public Map<String, FieldTransformer> getAll() {
        return Map.copyOf(registry);
    }
}