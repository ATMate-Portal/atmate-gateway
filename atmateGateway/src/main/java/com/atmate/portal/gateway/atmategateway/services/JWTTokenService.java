package com.atmate.portal.gateway.atmategateway.services;

import com.atmate.portal.gateway.atmategateway.database.entitites.User;
import io.jsonwebtoken.Claims; // <<< IMPORTAR
import io.jsonwebtoken.ExpiredJwtException; // <<< IMPORTAR
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException; // <<< IMPORTAR
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException; // <<< IMPORTAR
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException; // <<< IMPORTAR
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger; // <<< IMPORTAR
import org.slf4j.LoggerFactory; // <<< IMPORTAR
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
@Slf4j
public class JWTTokenService {
    @Value("${app.jwt.secret}")
    private String jwtSecretString;

    @Value("${app.jwt.expiration-ms}")
    private int jwtExpirationMs;

    private Key jwtSecretKey;

    @PostConstruct
    public void init() {
        if (jwtSecretString == null || jwtSecretString.getBytes().length < 32) {
            this.jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        } else {
            this.jwtSecretKey = Keys.hmacShaKeyFor(jwtSecretString.getBytes());
        }
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail()) // O 'subject' é geralmente o identificador principal
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // Método para obter todos os claims
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserIdFromToken(String token) {
        Number userIdNum = getAllClaimsFromToken(token).get("userId", Number.class);
        return userIdNum != null ? userIdNum.longValue() : null;
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            log.warn("Assinatura JWT inválida: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.warn("Token JWT inválido: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.warn("Token JWT expirado: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.warn("Token JWT não suportado: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.warn("String de claims JWT está vazia: {}", ex.getMessage());
        }
        return false;
    }
}