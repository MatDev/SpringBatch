package com.xanxs.engine_core_job.writer;

import com.xanxs.engine_core_job.domain.model.DestinationConfig;
import com.xanxs.engine_core_job.domain.model.ProcessedRecord;
import com.xanxs.engine_core_job.domain.model.TemplateConfig;
import com.xanxs.engine_core_job.error.JobErrorHandler;
import com.xanxs.engine_core_job.writer.api.DestinationWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

@Slf4j
public class CompositeDestinationWriter implements ItemWriter<ProcessedRecord> {

    private final TemplateConfig template;
    private final WriterRegistry writerRegistry;
    private final JobErrorHandler errorHandler;
    private final String executionId;

    public CompositeDestinationWriter(TemplateConfig template,
                                      WriterRegistry writerRegistry,
                                      JobErrorHandler errorHandler,
                                      String executionId) {
        this.template = template;
        this.writerRegistry = writerRegistry;
        this.errorHandler = errorHandler;
        this.executionId = executionId;
    }

    @Override
    public void write(Chunk<? extends ProcessedRecord> chunk) {
        List<? extends ProcessedRecord> records = chunk.getItems();

        // 1. Registrar errores de los records que fallaron en el processor
        records.stream()
                .filter(ProcessedRecord::isHasError)
                .forEach(record -> errorHandler.register(
                        executionId,
                        record.getLineNumber(),
                        record.getRawLine(),
                        record.getErrorDescription(),
                        record.getExceptionDetail()
                ));

        // 2. Escribir en cada destino configurado
        for (DestinationConfig destination : template.getDestinations()) {
            try {
                DestinationWriter writer = writerRegistry.get(
                        destination.getDestinationType());
                writer.write((List<ProcessedRecord>) records, destination);

            } catch (Exception e) {
                log.error("Error escribiendo en destino '{}': {}",
                        destination.getName(), e.getMessage());

                if ("STOP".equals(template.getOnError())) {
                    throw new RuntimeException(
                            "Error fatal en escritura a destino: " +
                                    destination.getName(), e);
                }
                // CONTINUE — loguea y sigue con el siguiente destino
            }
        }
    }
}