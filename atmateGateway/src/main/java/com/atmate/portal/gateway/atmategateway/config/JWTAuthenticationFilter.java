package com.atmate.portal.gateway.atmategateway.config;

import com.atmate.portal.gateway.atmategateway.services.JWTTokenService; // O teu serviço de token
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User; // User do Spring Security
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections; // Para roles vazias, se não estiveres a usar roles complexas

@Component // <<< IMPORTANTE: Para que o Spring o detete e o possas injetar
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger loggerFilter = LoggerFactory.getLogger(JWTAuthenticationFilter.class);

    @Autowired
    private JWTTokenService tokenProvider;

    // Opcional: Se quiseres carregar detalhes do utilizador da BD a cada pedido.
    // Para simplificar, vamos criar um UserDetails básico com o email do token.
    // @Autowired
    // private UserDetailsService userDetailsService; // Precisarias de implementar esta interface

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            loggerFilter.debug("Request URI: {}", request.getRequestURI());
            loggerFilter.debug("JWT extraído do header: {}", jwt);


            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String userEmail = tokenProvider.getEmailFromToken(jwt);
                loggerFilter.debug("Email extraído do token: {}", userEmail);

                // Aqui, estás a criar um UserDetails básico.
                // A password é "" porque a autenticação já foi feita pelo token.
                // Se usares roles, precisas de as extrair do token ou carregá-las
                // e passá-las como terceiro argumento em vez de Collections.emptyList().
                UserDetails userDetails = new User(userEmail, "", Collections.emptyList());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                loggerFilter.debug("Utilizador {} autenticado via JWT e SecurityContextHolder atualizado.", userEmail);
            } else {
                if (StringUtils.hasText(jwt)) {
                    loggerFilter.warn("Validação do token JWT falhou para o token: {}", jwt);
                } else {
                    // Não é um erro se for um endpoint público ou o login
                    if (!request.getRequestURI().startsWith("/auth/")) {
                        loggerFilter.debug("Nenhum JWT encontrado no header para o pedido protegido: {}", request.getRequestURI());
                    }
                }
            }
        } catch (Exception ex) {
            // Não relançar a exceção aqui para não quebrar a cadeia de filtros,
            // a menos que queiras um tratamento de erro de autenticação global.
            // O Spring Security tratará da recusa de acesso se a autenticação não for definida.
            loggerFilter.error("Não foi possível definir a autenticação do utilizador no contexto de segurança: {}", ex.getMessage(), ex);
        }

        filterChain.doFilter(request, response); // <<< IMPORTANTE: Continua a cadeia de filtros
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer "
        }
        return null;
    }
}