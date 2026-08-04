package com.xanxs.engine_core_job.api.exception;


public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, String id) {
        super(String.format("%s con id '%s' no encontrado", resource, id));
    }
}