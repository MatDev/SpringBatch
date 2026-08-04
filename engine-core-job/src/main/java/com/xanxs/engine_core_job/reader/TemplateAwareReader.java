package com.xanxs.engine_core_job.reader;
import com.xanxs.engine_core_job.domain.model.TemplateConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class TemplateAwareReader {

    private final FixedLengthLineMapper fixedMapper;
    private final DelimitedLineMapper delimitedMapper;
    private final TemplateConfig template;

    public TemplateAwareReader(TemplateConfig template) {
        this.template = template;
        this.fixedMapper = new FixedLengthLineMapper(template);
        this.delimitedMapper = new DelimitedLineMapper(template);
    }

    // Recibe la línea raw y retorna los campos extraídos
    public Map<String, String> mapLine(String line) {
        return switch (template.getFileType()) {
            case "FIXED"     -> fixedMapper.map(line);
            case "DELIMITED" -> delimitedMapper.map(line);
            default -> throw new IllegalStateException(
                    "Tipo de archivo no soportado: " + template.getFileType()
            );
        };
    }
}