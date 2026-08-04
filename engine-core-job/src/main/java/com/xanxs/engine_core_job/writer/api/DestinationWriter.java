package com.xanxs.engine_core_job.writer.api;


import com.xanxs.engine_core_job.domain.model.DestinationConfig;
import com.xanxs.engine_core_job.domain.model.ProcessedRecord;

import java.util.List;

public interface DestinationWriter {

    // Tipo de destino que maneja
    String getDestinationType();

    // Escribe un chunk completo
    void write(List<ProcessedRecord> records, DestinationConfig destination);

    // Inicialización opcional (conexiones, preparar statements, etc)
    default void init(DestinationConfig destination) {}

    // Cierre opcional
    default void close(DestinationConfig destination) {}
}