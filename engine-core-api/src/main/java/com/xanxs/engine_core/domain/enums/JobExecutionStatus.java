package com.xanxs.engine_core.domain.enums;


public enum JobExecutionStatus {
    PENDING,    // Solicitud recibida, aún no enviada al core
    RUNNING,    // Core procesando
    COMPLETED,  // Finalizado sin errores fatales
    FAILED,     // Falló con error fatal
    STOPPED     // Detenido manualmente
}