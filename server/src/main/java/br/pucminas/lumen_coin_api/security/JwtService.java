package br.pucminas.lumen_coin_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import jakarta.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${app.security.jwt.secret}")
    private String jwtSecret;

    @Value("${app.security.jwt.expiration-ms:86400000}")
    private long jwtExpirationMs;

    @Value("${app.security.jwt.cookie-name:lumen_auth}")
    private String cookieName;

    @Value("${app.security.jwt.cookie-secure:false}")
    private boolean cookieSecure;

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserPrincipal principal) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(principal.getUserId().toString())
                .claim("email", principal.getUsername())
                .claim("role", principal.getRole().name())
                .claim("name", principal.getName())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(jwtExpirationMs)))
                .signWith(signingKey())
                .compact();
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(getClaims(token).getSubject());
    }

    public String extractEmail(String token) {
        return getClaims(token).get("email", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public long getExpirationMs() {
        return jwtExpirationMs;
    }

    public String getCookieName() {
        return cookieName;
    }

    public Cookie buildAuthCookie(UserPrincipal principal) {
        Cookie cookie = new Cookie(cookieName, generateToken(principal));
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtExpirationMs / 1000));
        return cookie;
    }

    public Cookie buildClearCookie() {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setHttpOnly(true);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
