package com.atmate.portal.gateway.atmategateway.config;

import com.atmate.portal.gateway.atmategateway.services.JWTTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JWTTokenService tokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwt = getJwtFromRequest(request);
            log.warn("Request URI: {}", request.getRequestURI());
            log.warn("JWT extraído do header: {}", jwt);


            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String userEmail = tokenProvider.getEmailFromToken(jwt);
                log.warn("Email extraído do token: {}", userEmail);

                UserDetails userDetails = new User(userEmail, "", Collections.emptyList());

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.warn("Utilizador {} autenticado via JWT e SecurityContextHolder atualizado.", userEmail);
            } else {
                if (StringUtils.hasText(jwt)) {
                    log.warn("Validação do token JWT falhou para o token: {}", jwt);
                } else {
                    // Não é um erro se for um endpoint público ou o login
                    if (!request.getRequestURI().startsWith("/auth/")) {
                        log.warn("Nenhum JWT encontrado no header para o pedido protegido: {}", request.getRequestURI());
                    }
                }
            }
        } catch (Exception ex) {
            // Não relançar a exceção aqui para não quebrar a cadeia de filtros
            log.warn("Não foi possível definir a autenticação do utilizador no contexto de segurança: {}", ex.getMessage(), ex);
        }

        filterChain.doFilter(request, response); // Continua a cadeia de filtros
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer "
        }
        return null;
    }
}