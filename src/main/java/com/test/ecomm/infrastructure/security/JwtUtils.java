package com.test.ecomm.infrastructure.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.http.HttpServletRequest;
import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private int jwtExpirationMs;

    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    private static final String ACCESS_COOKIE  = "access_token";
    private static final String REFRESH_COOKIE = "refresh_token";

    // ─── Cookie yaratmaq ───────────────────────────────────────────
    public ResponseCookie generateAccessCookie(String email) {
        String token = buildToken(email, jwtExpirationMs);
        return buildCookie(ACCESS_COOKIE, token, jwtExpirationMs / 1000);
    }

    public ResponseCookie generateRefreshCookie(String email) {
        String token = buildToken(email, refreshExpirationMs);
        return buildCookie(REFRESH_COOKIE, token, refreshExpirationMs / 1000);
    }

    public ResponseCookie clearAccessCookie() {
        return buildCookie(ACCESS_COOKIE, "", 0);
    }

    public ResponseCookie clearRefreshCookie() {
        return buildCookie(REFRESH_COOKIE, "", 0);
    }

    // ─── Cookie-dən token oxumaq ────────────────────

    public String getAccessTokenFromCookie(HttpServletRequest request) {
        return getTokenFromCookie(request, ACCESS_COOKIE);
    }
    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        return getTokenFromCookie(request, REFRESH_COOKIE);
    }

    // ─── Token məlumatları ─────────────────────────────────────────

    public String getEmailFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("JWT müddəti bitib: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("JWT dəstəklənmir: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("JWT deformasiyaya uğrayıb: {}", e.getMessage());
        } catch (JwtException e) {
            log.warn("JWT xətası: {}", e.getMessage());
        }
        return false;
    }

    // ─── Private köməkçi metodlar ───────────────────────

    private String buildToken(String subject, long expirationMs) {
        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    private ResponseCookie buildCookie(String name, String value, long maxAge) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                // Production-da true olacaq - yalnız HTTPS-də göndərilir
                // Localhost-da false saxlayırıq
                .secure(false)
                .path("/")
                .maxAge(maxAge)
                .sameSite("Strict")
                .build();
    }

    // Token-i açıb içindəki məlumatları (Claims) qaytaran metod
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // application.yaml-dakı BASE64 string-i real kriptografik açara çevirir
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    private String getTokenFromCookie(HttpServletRequest request, String cookieName) {
        var cookie = WebUtils.getCookie(request, cookieName);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }
}