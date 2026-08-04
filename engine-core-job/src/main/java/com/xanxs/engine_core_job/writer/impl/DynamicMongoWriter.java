package com.xanxs.engine_core_job.writer.impl;

import com.xanxs.engine_core_job.domain.model.DestinationConfig;
import com.xanxs.engine_core_job.domain.model.ProcessedRecord;
import com.xanxs.engine_core_job.writer.api.DestinationWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicMongoWriter implements DestinationWriter {

    private final MongoTemplate mongoTemplate;

    @Override
    public String getDestinationType() {
        return "MONGODB";
    }

    @Override
    public void write(List<ProcessedRecord> records, DestinationConfig destination) {
        List<ProcessedRecord> valid = records.stream()
                .filter(r -> !r.isHasError())
                .toList();

        if (valid.isEmpty()) return;

        String collection = destination.getTargetCollection();

        // Convertir cada record a Document usando el mapping
        List<Document> documents = valid.stream()
                .map(record -> buildDocument(record, destination))
                .toList();

        // Bulk insert en una sola operación
        mongoTemplate.insert(documents, collection);

        log.debug("Escritos {} documentos en colección '{}'",
                documents.size(), collection);
    }

    private Document buildDocument(ProcessedRecord record,
                                   DestinationConfig destination) {
        Document doc = new Document();

        destination.getMappings().forEach((fieldName, targetField) -> {
            Object value = record.getFields().get(fieldName);
            doc.put(targetField, value);
        });

        return doc;
    }
}