package com.restaurante.service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(io.jsonwebtoken.io.Decoders.BASE64.decode(secret));
    }

    public String generarToken(UserDetails userDetails) {
        String rol = userDetails.getAuthorities().stream()
            .findFirst()
            .map(auth -> auth.getAuthority())
            .orElse("CLIENTE");

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("role", rol) // guardamos el rol dentro del payload
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String extraerUsername(String token) {
        return extraerClaims(token).getSubject();
    }

    public boolean validarToken(String token, UserDetails userDetails) {
        final String username = extraerUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpirado(token));
    }

    private boolean isTokenExpirado(String token) {
        return extraerClaims(token).getExpiration().before(new Date());
    }

    private Claims extraerClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
