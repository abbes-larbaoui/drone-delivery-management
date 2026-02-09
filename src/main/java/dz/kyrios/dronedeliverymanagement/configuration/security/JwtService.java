package dz.kyrios.dronedeliverymanagement.configuration.security;

import dz.kyrios.dronedeliverymanagement.statics.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtService {

    private static final String SECRET = "my-super-secret-key-my-super-secret-key"; // >= 32 chars

    private static final long EXPIRATION_MS = 60 * 60 * 1000; // 1 hour

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String generateToken(String name, UserRole type) {
        return Jwts.builder()
                .setSubject(name)
                .claim("role", type)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}

