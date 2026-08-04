package com.xanxs.engine_core.client;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Batch Engine - Job API")
                        .description("API para lanzar y monitorear ejecuciones del motor batch")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Batch Engine")
                                .email("admin@xanxs.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8082")
                                .description("Servidor local")
                ));
    }
}
