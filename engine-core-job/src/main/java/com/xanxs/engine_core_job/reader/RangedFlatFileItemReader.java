package com.xanxs.engine_core_job.reader;

import com.xanxs.engine_core_job.domain.model.TemplateConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

@Slf4j
public class RangedFlatFileItemReader implements ItemStreamReader<String> {

    private final String filePath;
    private final long startLine;
    private final long endLine;
    private final String encoding;
    private final boolean skipEmptyLines;

    private BufferedReader reader;
    private long currentLine = 0;
    private long linesRead = 0;

    public RangedFlatFileItemReader(String filePath,
                                    long startLine,
                                    long endLine,
                                    TemplateConfig template) {
        this.filePath = filePath;
        this.startLine = startLine;
        this.endLine = endLine;
        this.encoding = template.getEncoding();
        this.skipEmptyLines = Boolean.TRUE.equals(template.getSkipEmptyLines());
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        try {
            reader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(filePath),
                            Charset.forName(encoding)
                    )
            );

            // Saltar hasta la línea de inicio de esta partición
            skipToStartLine();

            log.debug("Reader abierto en partición líneas {} → {}",
                    startLine, endLine);

        } catch (Exception e) {
            throw new ItemStreamException(
                    "Error al abrir archivo: " + filePath, e);
        }
    }

    @Override
    public String read() throws Exception {
        if (currentLine > endLine) {
            return null; // fin de esta partición
        }

        String line;
        while ((line = reader.readLine()) != null) {
            currentLine++;

            if (currentLine > endLine) {
                return null; // pasamos el límite
            }

            // Saltar líneas vacías si está configurado
            if (skipEmptyLines && line.isBlank()) {
                continue;
            }

            linesRead++;
            return line;
        }

        return null; // fin del archivo
    }

    @Override
    public void update(ExecutionContext executionContext) {
        // Guardar progreso para restart en caso de fallo
        executionContext.putLong("currentLine", currentLine);
        executionContext.putLong("linesRead", linesRead);
    }

    @Override
    public void close() throws ItemStreamException {
        if (reader != null) {
            try {
                reader.close();
                log.debug("Reader cerrado. Líneas leídas: {}", linesRead);
            } catch (Exception e) {
                log.warn("Error al cerrar reader: {}", e.getMessage());
            }
        }
    }

    private void skipToStartLine() throws Exception {
        currentLine = 0;
        // Saltar líneas anteriores al rango de esta partición
        while (currentLine < startLine - 1) {
            String skipped = reader.readLine();
            if (skipped == null) break;
            currentLine++;
        }
        log.debug("Saltadas {} líneas hasta inicio de partición", currentLine);
    }
}