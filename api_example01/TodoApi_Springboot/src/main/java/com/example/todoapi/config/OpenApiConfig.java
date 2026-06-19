package com.example.todoapi.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI todoApiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Todo API")
                        .version("v1")
                        .description("A simple CRUD API for managing to-do items backed by MySQL."));
    }
}
