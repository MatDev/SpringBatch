package com.xanxs.config_api.config;



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
                        .title("Batch Engine - Config API")
                        .description("API para gestión de plantillas, campos, transformaciones y destinos del motor batch")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Batch Engine")
                                .email("admin@xanxs.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081")
                                .description("Servidor local")
                ));
    }
}