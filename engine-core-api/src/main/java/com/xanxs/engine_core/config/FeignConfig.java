package com.xanxs.engine_core.config;




import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    // Nivel de log para ver requests/responses del Feign en DEBUG
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    // Manejo de errores del core
    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            if (response.status() == 404) {
                return new RuntimeException(
                        "Job no encontrado en el core: " + methodKey
                );
            }
            if (response.status() == 400) {
                return new RuntimeException(
                        "Solicitud inválida al core: " + methodKey
                );
            }
            return new RuntimeException(
                    "Error inesperado al comunicarse con el core [" + response.status() + "]"
            );
        };
    }
}