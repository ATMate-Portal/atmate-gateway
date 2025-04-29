package com.atmate.portal.gateway.atmategateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// import org.springframework.web.cors.CorsConfigurationSource; // Não estritamente necessário aqui
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 🔐 Permitir credenciais (cookies, headers de autenticação, etc.)
        config.setAllowCredentials(true);

        // 🌍 Permitir APENAS as origens específicas do seu frontend
        //    Substitua "http://localhost:5173" pela URL real do seu frontend dev
        //    Adicione outras URLs se necessário (ex: produção)
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://atmate.sytes.net:8180"
        ));
        // config.setAllowedOriginPatterns(List.of("*")); // <<< REMOVIDO/COMENTADO

        // ✅ Métodos HTTP permitidos (GET, POST, PUT, DELETE, OPTIONS são os mais comuns)
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));

        // ✅ Cabeçalhos permitidos (Permitir todos é geralmente seguro para desenvolvimento)
        config.setAllowedHeaders(List.of("*"));

        // ✅ Cabeçalhos expostos (Permite ao frontend ler estes cabeçalhos da resposta)
        //    Adicione outros se o frontend precisar deles (ex: para paginação, tokens)
        config.setExposedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta configuração a todos os endpoints ("/**")
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}