package com.rozgar.backend.auth.service;


import com.rozgar.backend.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class JwtService {

    @Value("${rozgar.jwt.secret}")
    private String secret;

    @Value("${rozgar.jwt.expiry-ms}")
    private long expiryMs;

    //Token generation
    public String generateToken(User user){
        Map<String, Object> claims = new HashMap<>();
        claims.put("role",user.getRole().name());
        claims.put("userId",user.getId());

        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiryMs))
                .signWith(signingKey())
                .compact();
    }


    //Token Validator
    public boolean isTokenValid(String token, User user){
        try{
            String email = extractEmail(token);
            return email.equals(user.getEmail()) && !isExpired(token);
        } catch (Exception e){
            log.warn("JWT validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String extractEmail(String token){
        return extractAllClaims(token).getSubject();
    }

    private boolean isExpired(String token){
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey signingKey(){
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
