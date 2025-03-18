package br.com.devluisoliveira.agenteroteiro.shared.configs.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.stream.Collectors;

@Log4j2
@Component
public class JwtProvider {

    @Value("${roteiro.auth.jwtSecret}")
    private String jwtSecret;
    @Value("${roteiro.auth.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwt(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        // 1. Codificar as roles em Base64 individualmente e depois juntá-las
        final String encodedRoles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> Base64.getEncoder().encodeToString(role.getBytes(StandardCharsets.UTF_8))) // Codifica cada
                                                                                                        // role em
                                                                                                        // Base64
                .collect(Collectors.joining(",")); // Junta as roles codificadas

        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        return Jwts.builder()
                .setSubject((userPrincipal.getUserId().toString()))
                .claim("roles", encodedRoles) // Usa as roles codificadas em Base64
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getSubjectJwt(String token) {
        // 2. Decodificar as roles ao ler o token
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String subject = claims.getSubject();

        // Decodifica as roles
        String encodedRoles = claims.get("roles", String.class);
        if (encodedRoles != null) {
            String decodedRoles = Arrays.stream(encodedRoles.split(","))
                    .map(encodedRole -> new String(Base64.getDecoder().decode(encodedRole), StandardCharsets.UTF_8))
                    .collect(Collectors.joining(","));
            log.info("Decoded roles: {}", decodedRoles); // Apenas para fins de debug
        }

        return subject;
    }

    public boolean validateJwt(String authToken) {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
            SecretKey key = Keys.hmacShaKeyFor(keyBytes);

            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature or token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}