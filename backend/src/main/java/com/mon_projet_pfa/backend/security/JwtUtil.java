package com.mon_projet_pfa.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    // ✅ Clé plus longue et sécurisée (minimum 256 bits pour HS256)
    private final String SECRET_KEY = "monSuperSecretKeyQuiEstTresLongueEtSecuriseePourJWTAuthentification123456789AbCdEfGhIjKlMnOpQrStUvWxYz";
    private final long EXPIRATION_TIME = 86400000; // 24 heures

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(String email) {
        try {
            log.info("🔄 Début génération token pour: {}", email);

            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

            log.info("📅 Date création: {}", now);
            log.info("📅 Date expiration: {}", expiryDate);

            // ✅ NOUVELLE API JWT (version 0.11+)
            String token = Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256) // ✅ Nouvelle méthode
                    .compact();

            log.info("✅ Token généré avec succès, longueur: {}", token.length());
            log.info("🔑 Token preview: {}...", token.substring(0, Math.min(50, token.length())));

            return token;

        } catch (Exception e) {
            log.error("❌ ERREUR lors de la génération du token pour {}: {}", email, e.getMessage(), e);

            // Log détaillé de l'erreur
            if (e instanceof SecurityException) {
                log.error("❌ Problème de sécurité: {}", e.getMessage());
            } else if (e instanceof IllegalArgumentException) {
                log.error("❌ Argument invalide: {}", e.getMessage());
            }

            throw new RuntimeException("Impossible de générer le token: " + e.getMessage(), e);
        }
    }

    public String extractEmail(String token) {
        try {
            log.info("🔍 Extraction email du token...");

            // ✅ NOUVELLE API JWT
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String email = claims.getSubject();
            log.info("✅ Email extrait: {}", email);
            return email;

        } catch (Exception e) {
            log.error("❌ Erreur extraction email: {}", e.getMessage(), e);
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            log.info("🔍 Validation du token...");

            if (token == null || token.trim().isEmpty()) {
                log.warn("⚠️ Token vide ou null");
                return false;
            }

            // ✅ NOUVELLE API JWT
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);

            log.info("✅ Token valide");
            return true;

        } catch (ExpiredJwtException e) {
            log.warn("⏰ Token expiré: {}", e.getMessage());
            return false;
        } catch (JwtException e) {
            log.warn("❌ Token invalide: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("❌ Erreur validation token: {}", e.getMessage(), e);
            return false;
        }
    }

    public Date extractExpiration(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration();
        } catch (Exception e) {
            log.error("❌ Erreur extraction expiration: {}", e.getMessage());
            return null;
        }
    }
}