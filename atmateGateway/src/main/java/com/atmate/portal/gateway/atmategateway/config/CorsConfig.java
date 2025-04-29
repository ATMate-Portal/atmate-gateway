package com.atmate.portal.gateway.atmategateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 🔐 Permitir credenciais (cookies, headers de autenticação, etc.)
        config.setAllowCredentials(true);

        // 🌍 Permitir apenas o frontend local (altera isto se for outro domínio em produção)
        config.setAllowedOriginPatterns(List.of("*"));

        // ✅ Métodos HTTP permitidos
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // ✅ Cabeçalhos permitidos
        config.setAllowedHeaders(List.of("*"));

        // ✅ Cabeçalhos expostos
        config.setExposedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // aplica a todos os endpoints

        return new CorsFilter(source);
    }
}

