package com.atmate.portal.gateway.atmategateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 🔐 Permitir credenciais (importante se usar cookies, tokens de autenticação, etc.)
        config.setAllowCredentials(true);

        // 🌍 !!! DEFINIR AS ORIGENS PERMITIDAS !!!
        // Apenas as URLs EXATAS do seu frontend (local e produção).
        // NÃO use "*" com allowCredentials=true.
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",  // <--- Mantenha esta para desenvolvimento local do React (ajuste a porta se for diferente)
                "https://atmate.online"   // <--- ESTA É A URL DE PRODUÇÃO DO SEU FRONTEND (sem barra no final é mais comum)
                // Remova as URLs antigas (sytes.net, IP direto) pois não são mais necessárias ou seguras nesta configuração.
        ));

        // ✅ Métodos HTTP permitidos
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));

        // ✅ Cabeçalhos permitidos (* é geralmente aceitável aqui, mas pode ser mais restrito se preferir)
        config.setAllowedHeaders(List.of("*"));

        // ✅ Cabeçalhos expostos na resposta que o frontend pode ler
        config.setExposedHeaders(List.of("Authorization", "Content-Type")); // Adicione outros se precisar

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta configuração a todos os paths ("/**") da sua API
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // O CorsFilter geralmente não é necessário se estiver a usar Spring Security com http.cors(),
    // pois ele usará o Bean corsConfigurationSource() acima.
    /*
    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }
    */
}