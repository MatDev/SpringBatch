package com.xanxs.engine_core_job.writer;


import com.xanxs.engine_core_job.writer.api.DestinationWriter;
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
public class WriterRegistry {

    private final List<DestinationWriter> writers;
    private Map<String, DestinationWriter> registry;

    @PostConstruct
    public void init() {
        registry = writers.stream()
                .collect(Collectors.toMap(
                        DestinationWriter::getDestinationType,
                        Function.identity()
                ));

        log.info("WriterRegistry inicializado con {} writers: {}",
                registry.size(), registry.keySet());
    }

    public DestinationWriter get(String destinationType) {
        DestinationWriter writer = registry.get(destinationType);
        if (writer == null) {
            throw new IllegalArgumentException(
                    String.format("Writer para tipo '%s' no encontrado. " +
                            "Disponibles: %s", destinationType, registry.keySet())
            );
        }
        return writer;
    }
}
