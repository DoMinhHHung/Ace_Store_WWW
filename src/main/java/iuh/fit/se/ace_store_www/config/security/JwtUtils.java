package iuh.fit.se.ace_store_www.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

@Component
public class JwtUtils {

    @Value("${jwt.secret:${app.jwtSecret:}}")
    private String jwtSecret;

    @Value("${jwt.expiration:${app.jwtExpirationMs:86400000}}")
    private long jwtExpirationMs;

    @Value("${jwt.refresh-token.expiration:${app.jwtRefreshTokenExpirationMs:604800000}}")
    private long jwtRefreshExpirationMs;

    private SecretKey signingKey;

    @PostConstruct
    public void init() {
        if (jwtSecret == null || jwtSecret.isBlank()) {
            throw new IllegalStateException("JWT secret is not configured. Set jwt.secret or app.jwtSecret property.");
        }
        byte[] keyBytes = decodeSecretToBytes(jwtSecret);
        signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, Set<String> roles) {
        Claims claims = Jwts.claims();
        claims.setSubject(username);
        claims.put("roles", roles);
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(String username) {
        Claims claims = Jwts.claims();
        claims.setSubject(username);
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtRefreshExpirationMs);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(signingKey, SignatureAlgorithm.HS512)
                .compact();
    }
    public String getEmailFromJwt(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    public Claims getAllClaimsFromJwt(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    private static byte[] decodeSecretToBytes(String secret) {
        String trimmed = secret.trim();
        if (trimmed.matches("(?i)^[0-9a-f]+$") && (trimmed.length() % 2 == 0)) {
            return hexStringToByteArray(trimmed);
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(trimmed);
            if (decoded.length > 0) return decoded;
        } catch (IllegalArgumentException ignored) {
        }
        return trimmed.getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            int hi = Character.digit(s.charAt(i), 16);
            int lo = Character.digit(s.charAt(i + 1), 16);
            if (hi == -1 || lo == -1) throw new IllegalArgumentException("Invalid hex character in JWT secret");
            data[i / 2] = (byte) ((hi << 4) + lo);
        }
        return data;
    }
}