package com.xanxs.engine_core_job.reader;


import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FilePartitioner implements Partitioner {

    private final String filePath;
    private final boolean skipEmptyLines;

    public FilePartitioner(String filePath, boolean skipEmptyLines) {
        this.filePath = filePath;
        this.skipEmptyLines = skipEmptyLines;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        log.info("Particionando archivo '{}' en {} partes", filePath, gridSize);

        long totalLines = countLines();
        log.info("Total de líneas en archivo: {}", totalLines);

        Map<String, ExecutionContext> partitions = new HashMap<>();

        long linesPerPartition = totalLines / gridSize;
        long remainder = totalLines % gridSize;

        long startLine = 1;

        for (int i = 0; i < gridSize; i++) {
            // La última partición absorbe el remanente
            long endLine = startLine + linesPerPartition - 1;
            if (i == gridSize - 1) {
                endLine += remainder;
            }

            ExecutionContext context = new ExecutionContext();
            context.putString("filePath", filePath);
            context.putLong("startLine", startLine);
            context.putLong("endLine", endLine);
            context.putInt("partitionIndex", i);

            partitions.put("partition-" + i, context);

            log.debug("Partición {}: líneas {} → {}", i, startLine, endLine);

            startLine = endLine + 1;
        }

        return partitions;
    }

    private long countLines() {
        long count = 0;
        try (BufferedReader reader = new BufferedReader(
                new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (skipEmptyLines && line.isBlank()) continue;
                count++;
            }
        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se puede leer el archivo para particionar: " + filePath, e);
        }
        return count;
    }
}