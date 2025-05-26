package com.atmate.portal.gateway.atmategateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
// O CorsFilter pode não ser mais necessário se o Spring Security aplicar a configuração
// import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    /**
     * Define a fonte de configuração CORS como um Bean.
     * O Spring Security (via http.cors()) irá detetar e usar este Bean automaticamente.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 🔐 Permitir credenciais (necessário para cookies, Auth headers, etc.)
        config.setAllowCredentials(true);

        // 🌍 !!! IMPORTANTE: DEFINIR A ORIGEM EXATA DO FRONTEND !!!
        // Substitua "http://localhost:5173" pela URL exata onde o seu React App corre.
        // Adicione outras URLs de produção se necessário. NÃO USE "*" com allowCredentials=true.
        config.setAllowedOrigins(List.of(
                "http://localhost:5173" // <--- VERIFIQUE E AJUSTE ESTA URL
                , "http://atmate.sytes.net/"
                , "http://atmate.sytes.net:4173/"// Exemplo produção
                , "http://85.241.132.174/"
                , "https://atmate.online/"
        ));
        // config.setAllowedOriginPatterns(List.of("*")); // <-- REMOVER/COMENTAR

        // ✅ Métodos HTTP permitidos
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));

        // ✅ Cabeçalhos permitidos (* é geralmente aceitável aqui)
        config.setAllowedHeaders(List.of("*"));

        // ✅ Cabeçalhos expostos na resposta que o frontend pode ler
        config.setExposedHeaders(List.of("Authorization", "Content-Type")); // Adicione outros se precisar

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplica esta configuração a todos os paths ("/**")
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // --- O Bean CorsFilter provavelmente já NÃO é necessário ---
    // O Spring Security aplicará a configuração definida em corsConfigurationSource()
    // Comente ou remova este Bean se estiver a usar http.cors() no SecurityConfig.
    /*
    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }
    */
}