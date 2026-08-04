package com.xanxs.engine_core_job.processor.validator;

import com.xanxs.engine_core_job.domain.model.ProcessedRecord;
import com.xanxs.engine_core_job.domain.model.TemplateConfig;
import com.xanxs.engine_core_job.processor.FieldExtractor;
import com.xanxs.engine_core_job.reader.TemplateAwareReader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.util.Map;

@Slf4j
public class RecordProcessor implements ItemProcessor<String, ProcessedRecord> {

    private final TemplateConfig template;
    private final FieldExtractor fieldExtractor;
    private final RecordValidator validator;
    private final TemplateAwareReader reader;

    // Contador de línea dentro del worker
    private int lineNumber = 0;

    public RecordProcessor(TemplateConfig template,
                           FieldExtractor fieldExtractor,
                           RecordValidator validator,
                           int startLine) {
        this.template = template;
        this.fieldExtractor = fieldExtractor;
        this.validator = validator;
        this.reader = new TemplateAwareReader(template);
        this.lineNumber = startLine;
    }

    @Override
    public ProcessedRecord process(String line) {
        lineNumber++;

        try {
            // 1. Extraer y transformar campos
            Map<String, Object> fields = fieldExtractor.extract(
                    line, template, reader);

            // 2. Construir record
            ProcessedRecord record = ProcessedRecord.builder()
                    .lineNumber(lineNumber)
                    .rawLine(line)
                    .fields(fields)
                    .hasError(false)
                    .build();

            // 3. Validar
            validator.validate(record, template);

            return record;

        } catch (Exception ex) {
            log.warn("Error procesando línea {}: {}", lineNumber, ex.getMessage());

            // Si onError = STOP relanzamos la excepción
            if ("STOP".equals(template.getOnError())) {
                throw new RuntimeException(
                        String.format("Error fatal en línea %d: %s",
                                lineNumber, ex.getMessage()), ex);
            }

            // Si onError = CONTINUE retornamos el record con error
            return ProcessedRecord.builder()
                    .lineNumber(lineNumber)
                    .rawLine(line)
                    .fields(Map.of())
                    .hasError(true)
                    .errorDescription(ex.getMessage())
                    .exceptionDetail(getStackTrace(ex))
                    .build();
        }
    }

    private String getStackTrace(Exception ex) {
        StringBuilder sb = new StringBuilder();
        sb.append(ex.getClass().getName()).append(": ").append(ex.getMessage());
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append("\n\tat ").append(element);
        }
        return sb.toString();
    }
}