package com.atmate.portal.gateway.atmategateway.config; // Mantém o teu package

import org.springframework.beans.factory.annotation.Autowired; // <<< PARA INJETAR O FILTRO
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // <<< PARA REFERÊNCIA DE ONDE INSERIR O FILTRO

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    private static final String[] SWAGGER_WHITELIST_URLS = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    @Autowired // Ou usa injeção via construtor (preferível)
    public SecurityConfig(JWTAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/create").permitAll() // O teu endpoint de registo, correto
                        .requestMatchers(SWAGGER_WHITELIST_URLS).permitAll()
                        .anyRequest().authenticated() // Todas as outras rotas requerem autenticação
                )
                // V ADICIONAR O FILTRO JWT AQUI V
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
        // Este é o passo crucial que estava em falta. Ele instrui o Spring Security
        // a usar o teu JwtAuthenticationFilter para processar os pedidos ANTES
        // do filtro padrão de autenticação de username/password. O teu filtro
        // irá verificar o token JWT e configurar o contexto de segurança.

        return http.build();
    }
}