package com.xanxs.engine_core_job.writer.impl;

import com.xanxs.engine_core_job.domain.model.DestinationConfig;
import com.xanxs.engine_core_job.domain.model.ProcessedRecord;
import com.xanxs.engine_core_job.writer.api.DestinationWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicJdbcWriter implements DestinationWriter {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public String getDestinationType() {
        return "POSTGRESQL";
    }

    @Override
    public void write(List<ProcessedRecord> records, DestinationConfig destination) {
        // Filtrar registros con error
        List<ProcessedRecord> valid = records.stream()
                .filter(r -> !r.isHasError())
                .toList();

        if (valid.isEmpty()) return;

        // Construir INSERT dinámico basado en el mapping
        String sql = buildInsertSql(destination);
        log.debug("SQL dinámico: {}", sql);

        // Construir parámetros para cada registro
        MapSqlParameterSource[] batchParams = valid.stream()
                .map(record -> buildParams(record, destination))
                .toArray(MapSqlParameterSource[]::new);

        // Ejecutar batch insert en una sola operación
        jdbcTemplate.batchUpdate(sql, batchParams);

        log.debug("Escritos {} registros en tabla '{}'",
                valid.size(), destination.getTargetTable());
    }

    private String buildInsertSql(DestinationConfig destination) {
        Map<String, String> mappings = destination.getMappings();
        // mappings: fieldName → targetColumn

        String columns = String.join(", ", mappings.values());
        String params = mappings.values().stream()
                .map(col -> ":" + col)
                .collect(Collectors.joining(", "));

        return String.format("INSERT INTO %s (%s) VALUES (%s)",
                destination.getTargetTable(), columns, params);
    }

    private MapSqlParameterSource buildParams(ProcessedRecord record,
                                              DestinationConfig destination) {
        MapSqlParameterSource params = new MapSqlParameterSource();

        destination.getMappings().forEach((fieldName, targetColumn) -> {
            Object value = record.getFields().get(fieldName);
            params.addValue(targetColumn, value);
        });

        return params;
    }
}
