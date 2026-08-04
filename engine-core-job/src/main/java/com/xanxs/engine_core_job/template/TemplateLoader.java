package com.xanxs.engine_core_job.template;

// template/TemplateLoader.java


import com.xanxs.engine_core_job.domain.model.DestinationConfig;
import com.xanxs.engine_core_job.domain.model.FieldConfig;
import com.xanxs.engine_core_job.domain.model.TemplateConfig;
import com.xanxs.engine_core_job.domain.model.TransformationConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class TemplateLoader {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Cacheable(value = "templates", key = "#templateId")
    public TemplateConfig load(Long templateId) {
        log.debug("Cargando plantilla id: {} desde BD", templateId);

        // 1. Cargar plantilla base
        TemplateConfig template = loadTemplate(templateId);

        // 2. Cargar campos con transformaciones
        List<FieldConfig> fields = loadFields(templateId);
        template.setFields(fields);

        // 3. Cargar destinos con mappings
        List<DestinationConfig> destinations = loadDestinations(templateId);
        template.setDestinations(destinations);

        log.info("Plantilla '{}' cargada con {} campos y {} destinos",
                template.getName(), fields.size(), destinations.size());

        return template;
    }

    private TemplateConfig loadTemplate(Long templateId) {
        String sql = """
            SELECT id, name, file_type, separator, encoding,
                   skip_empty_lines, on_error
            FROM bte_template
            WHERE id = :id AND active = true
        """;

        return jdbcTemplate.queryForObject(sql,
                Map.of("id", templateId),
                (rs, rn) -> TemplateConfig.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .fileType(rs.getString("file_type"))
                        .separator(rs.getString("separator"))
                        .encoding(rs.getString("encoding"))
                        .skipEmptyLines(rs.getBoolean("skip_empty_lines"))
                        .onError(rs.getString("on_error"))
                        .build()
        );
    }

    private List<FieldConfig> loadFields(Long templateId) {
        // Cargar campos
        String fieldSql = """
            SELECT id, name, field_type, position, length,
                   required, default_value, order_index
            FROM bte_template_field
            WHERE template_id = :templateId
            ORDER BY order_index ASC
        """;

        List<FieldConfig> fields = jdbcTemplate.query(fieldSql,
                Map.of("templateId", templateId),
                (rs, rn) -> FieldConfig.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .fieldType(rs.getString("field_type"))
                        .position(rs.getInt("position"))
                        .length(rs.getObject("length") != null ? rs.getInt("length") : null)
                        .required(rs.getBoolean("required"))
                        .defaultValue(rs.getString("default_value"))
                        .orderIndex(rs.getInt("order_index"))
                        .transformations(new ArrayList<>())
                        .build()
        );

        if (fields.isEmpty()) return fields;

        // Cargar transformaciones de todos los campos en una sola query
        List<Long> fieldIds = fields.stream().map(FieldConfig::getId).toList();

        String transSql = """
            SELECT id, field_id, transformer_name, parameters::text, order_index
            FROM bte_field_transformation
            WHERE field_id IN (:fieldIds)
            ORDER BY field_id, order_index ASC
        """;

        Map<Long, List<TransformationConfig>> transByField = new HashMap<>();

        jdbcTemplate.query(transSql,
                Map.of("fieldIds", fieldIds),
                rs -> {
                    Long fieldId = rs.getLong("field_id");
                    TransformationConfig trans = TransformationConfig.builder()
                            .id(rs.getLong("id"))
                            .transformerName(rs.getString("transformer_name"))
                            .parameters(parseParameters(rs.getString("parameters")))
                            .orderIndex(rs.getInt("order_index"))
                            .build();

                    transByField.computeIfAbsent(fieldId, k -> new ArrayList<>()).add(trans);
                }
        );

        // Asignar transformaciones a cada campo
        fields.forEach(field ->
                field.setTransformations(
                        transByField.getOrDefault(field.getId(), Collections.emptyList())
                )
        );

        return fields;
    }

    private List<DestinationConfig> loadDestinations(Long templateId) {
        String destSql = """
            SELECT id, destination_type, name, target_table, target_collection,
                   target_topic, target_path, target_url
            FROM bte_destination
            WHERE template_id = :templateId AND active = true
        """;

        List<DestinationConfig> destinations = jdbcTemplate.query(destSql,
                Map.of("templateId", templateId),
                (rs, rn) -> DestinationConfig.builder()
                        .id(rs.getLong("id"))
                        .destinationType(rs.getString("destination_type"))
                        .name(rs.getString("name"))
                        .targetTable(rs.getString("target_table"))
                        .targetCollection(rs.getString("target_collection"))
                        .targetTopic(rs.getString("target_topic"))
                        .targetPath(rs.getString("target_path"))
                        .targetUrl(rs.getString("target_url"))
                        .mappings(new HashMap<>())
                        .build()
        );

        if (destinations.isEmpty()) return destinations;

        // Cargar mappings de todos los destinos en una sola query
        List<Long> destIds = destinations.stream().map(DestinationConfig::getId).toList();

        String mappingSql = """
            SELECT destination_id, field_name, target_column
            FROM bte_destination_mapping
            WHERE destination_id IN (:destIds)
            ORDER BY order_index ASC
        """;

        Map<Long, Map<String, String>> mappingsByDest = new HashMap<>();

        jdbcTemplate.query(mappingSql,
                Map.of("destIds", destIds),
                rs -> {
                    Long destId = rs.getLong("destination_id");
                    mappingsByDest
                            .computeIfAbsent(destId, k -> new LinkedHashMap<>())
                            .put(rs.getString("field_name"), rs.getString("target_column"));
                }
        );

        // Asignar mappings a cada destino
        destinations.forEach(dest ->
                dest.setMappings(
                        mappingsByDest.getOrDefault(dest.getId(), Collections.emptyMap())
                )
        );

        return destinations;
    }

    // Convierte el JSON de parameters a Map<String, String>
    @SuppressWarnings("unchecked")
    private Map<String, String> parseParameters(String json) {
        if (json == null || json.isBlank()) return Collections.emptyMap();
        try {
            // Parseo simple sin Jackson para no agregar dependencia
            // Jackson ya viene con Spring Boot
            com.fasterxml.jackson.databind.ObjectMapper mapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.readValue(json, Map.class);
        } catch (Exception e) {
            return Collections.emptyMap();
        }
    }
}
