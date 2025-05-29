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
import org.slf4j.Logger; // <<< IMPORTAR
import org.slf4j.LoggerFactory; // <<< IMPORTAR
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTTokenService {
    private static final Logger logger = LoggerFactory.getLogger(JWTTokenService.class); // <<< ADICIONAR LOGGER

    @Value("${app.jwt.secret}")
    private String jwtSecretString;

    @Value("${app.jwt.expiration-ms}")
    private int jwtExpirationMs;

    private Key jwtSecretKey;

    @PostConstruct
    public void init() {
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
        claims.put("userId", user.getId()); // Assume que User tem getId()
        claims.put("email", user.getEmail()); // Assume que User tem getEmail()
        // Adiciona outros claims se necessário, como user.getUsername() se for diferente do email
        // claims.put("username", user.getUsername());


        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail()) // O 'subject' é geralmente o identificador principal
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(jwtSecretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // VVV NOVOS MÉTODOS ADICIONADOS ABAIXO VVV

    // Método para obter todos os claims
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getUserIdFromToken(String token) {
        // Os claims numéricos podem ser lidos como Integer ou Long dependendo de como foram inseridos
        // e da biblioteca JWT. É mais seguro ler como Number e converter.
        Number userIdNum = getAllClaimsFromToken(token).get("userId", Number.class);
        return userIdNum != null ? userIdNum.longValue() : null;
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        // Retorna o 'subject' que definiste ao gerar o token, ou um claim específico.
        // Se o subject é o email, isto funciona.
        // Se guardaste o email num claim específico como "email", usa: claims.get("email", String.class)
        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecretKey).build().parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Assinatura JWT inválida: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            logger.error("Token JWT inválido: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Token JWT expirado: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Token JWT não suportado: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("String de claims JWT está vazia: {}", ex.getMessage());
        }
        return false;
    }
}