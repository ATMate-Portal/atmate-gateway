package com.atmate.portal.gateway.atmategateway.services;

import com.atmate.portal.gateway.atmategateway.database.entitites.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTTokenService {

    @Value("${app.jwt.secret}") // Define no application.properties ou application.yml
    private String jwtSecretString;

    @Value("${app.jwt.expiration-ms}") // Define no application.properties ou application.yml
    private int jwtExpirationMs;

    private Key jwtSecretKey;

    @PostConstruct
    public void init() {
        // Gera uma chave segura se a string do segredo for muito curta para HS256
        // Para produção, considera usar uma chave mais robusta ou gerada externamente.
        if (jwtSecretString == null || jwtSecretString.getBytes().length < 32) {
            this.jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
            System.err.println("AVISO: O segredo JWT fornecido é fraco ou nulo. Uma chave aleatória foi gerada. " +
                    "Configure 'app.jwt.secret' com pelo menos 256 bits no application.properties.");
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
                .setSubject(user.getEmail())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }


}