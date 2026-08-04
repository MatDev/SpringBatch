package com.xanxs.engine_core_job.job.registry;


import com.xanxs.engine_core_job.job.BatchJobDefinition;
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
public class BatchJobRegistry {

    // Spring inyecta todos los beans que implementan BatchJobDefinition
    private final List<BatchJobDefinition> jobDefinitions;

    private Map<String, BatchJobDefinition> registry;

    @PostConstruct
    public void init() {
        registry = jobDefinitions.stream()
                .collect(Collectors.toMap(
                        BatchJobDefinition::getJobName,
                        Function.identity()
                ));

        log.info("JobRegistry inicializado con {} jobs: {}",
                registry.size(), registry.keySet());
    }

    public BatchJobDefinition get(String jobName) {
        BatchJobDefinition definition = registry.get(jobName);
        if (definition == null) {
            throw new IllegalArgumentException(
                    String.format("Job '%s' no encontrado. " +
                            "Disponibles: %s", jobName, registry.keySet())
            );
        }
        return definition;
    }

    public boolean exists(String jobName) {
        return registry.containsKey(jobName);
    }

    public Map<String, BatchJobDefinition> getAll() {
        return Map.copyOf(registry);
    }
}