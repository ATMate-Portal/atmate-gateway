package com.atmate.portal.gateway.atmategateway.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth"; // Pode ser qualquer nome

        return new OpenAPI()
                // Adiciona a informação geral da API (título, versão)
                .info(new Info()
                        .title("ATMate Gateway API")
                        .version("1.0.0")
                        .description("API que serve os dados à aplicação WEB ATMate")
                )
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP) // Tipo HTTP
                                .scheme("bearer")               // Esquema Bearer
                                .bearerFormat("JWT")            // Formato do token
                        )
                );
    }
}