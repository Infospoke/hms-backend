package com.hms.service.utils;

import java.security.Key;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {

	public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655465675458576D5A71347437";

    public String generateToken(String email, String userName, String roleName,
                                List<String> permissions, Boolean firstTimeWebLogin ,Boolean firstTimeMobileLogin) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userName);
        claims.put("role", roleName);
        claims.put("permissions", permissions);
        claims.put("firstTimeWebLogin", firstTimeWebLogin);
        claims.put("firstTimeMobileLogin", firstTimeMobileLogin);

        return createToken(claims, email);
    }

    private String createToken(Map<String, Object> claims, String email) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 180))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    public Claims decodeToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractUsername(String token) {
        return decodeToken(token).getSubject();
    }
    
	public String extractUsernameFromClaims(String token) {
		return decodeToken(token).get("username", String.class);
	}

    public String extractRole(String token) {
        return decodeToken(token).get("role", String.class);
    }

    public List<String> extractPermissions(String token) {
        return decodeToken(token).get("permissions", List.class);
    }
}
