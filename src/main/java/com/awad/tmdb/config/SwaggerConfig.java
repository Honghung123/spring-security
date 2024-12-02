package com.awad.tmdb.config;

import com.awad.tmdb.constant.AppPropertiesConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {
    private final AppPropertiesConfig properties;

    private License getLicense() {
        return new License()
                .name(properties.getSwagger().getLicense().getName())
                .url(properties.getSwagger().getLicense().getUrl());
    }

    private Info getDocumentApiInfo() {
        return new Info()
                .title(properties.getSwagger().getDocument().getTitle())
                .version(properties.getSwagger().getDocument().getVersion())
                .description(properties.getSwagger().getDocument().getDescription())
                .license(this.getLicense());
    }

    private List<Server> getServerList() {
        return properties.getSwagger().getServers()
                .stream()
                .map(server -> new Server().url(server.getUrl()).description(server.getDescription()))
                .toList();
    }

    private SecurityScheme getSecurityScheme() {
        return new SecurityScheme()
                .name("bearerAuth")
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    private Components getComponents() {
        return new Components().addSecuritySchemes("bearerAuth", this.getSecurityScheme());
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(this.getDocumentApiInfo())
                .servers(this.getServerList())
                .components(this.getComponents());
    }
}
