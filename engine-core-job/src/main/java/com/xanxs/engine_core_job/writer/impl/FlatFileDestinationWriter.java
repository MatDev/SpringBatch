package com.xanxs.engine_core_job.writer.impl;

import com.xanxs.engine_core_job.domain.model.DestinationConfig;
import com.xanxs.engine_core_job.domain.model.ProcessedRecord;
import com.xanxs.engine_core_job.writer.api.DestinationWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class FlatFileDestinationWriter implements DestinationWriter {

    // Map de writers abiertos por destino
    private final Map<Long, BufferedWriter> openWriters =
            new java.util.concurrent.ConcurrentHashMap<>();

    @Override
    public String getDestinationType() {
        return "FILE";
    }

    @Override
    public void init(DestinationConfig destination) {
        try {
            String path = destination.getTargetPath();
            boolean append = destination.getExtraConfig() != null
                    && Boolean.parseBoolean(
                    String.valueOf(destination.getExtraConfig()
                            .getOrDefault("append", "false")));

            BufferedWriter writer = new BufferedWriter(
                    new FileWriter(path, append));

            openWriters.put(destination.getId(), writer);
            log.debug("Archivo de salida abierto: {}", path);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se puede abrir archivo de salida: " + destination.getTargetPath(), e);
        }
    }

    @Override
    public void write(List<ProcessedRecord> records, DestinationConfig destination) {
        List<ProcessedRecord> valid = records.stream()
                .filter(r -> !r.isHasError())
                .toList();

        if (valid.isEmpty()) return;

        BufferedWriter writer = openWriters.get(destination.getId());
        if (writer == null) {
            throw new IllegalStateException(
                    "Writer no inicializado para destino: " + destination.getName());
        }

        String separator = destination.getExtraConfig() != null
                ? String.valueOf(destination.getExtraConfig()
                .getOrDefault("separator", "|"))
                : "|";

        try {
            for (ProcessedRecord record : valid) {
                // Escribir campos en el orden del mapping
                String line = destination.getMappings().entrySet().stream()
                        .map(entry -> {
                            Object value = record.getFields().get(entry.getKey());
                            return value != null ? value.toString() : "";
                        })
                        .collect(Collectors.joining(separator));

                writer.write(line);
                writer.newLine();
            }
            writer.flush();

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Error escribiendo en archivo: " + destination.getTargetPath(), e);
        }
    }

    @Override
    public void close(DestinationConfig destination) {
        BufferedWriter writer = openWriters.remove(destination.getId());
        if (writer != null) {
            try {
                writer.close();
                log.debug("Archivo de salida cerrado: {}", destination.getTargetPath());
            } catch (IOException e) {
                log.warn("Error cerrando archivo: {}", e.getMessage());
            }
        }
    }
}
