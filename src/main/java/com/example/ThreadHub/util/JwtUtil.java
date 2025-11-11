package com.example.ThreadHub.util;

import com.example.ThreadHub.entity.Account;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private static final String SECRET = "jV0dVqz9M7h1fK3rP8s2W6x9Z4c1B7n0Q5t8L2y6R9u3E1a5C7k9M2p4T6v8X0z";
    private static final long EXPIRATION = 3600000; // 1 hour

    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public static String generateToken(Account account) {
        return Jwts.builder()
                .setSubject(account.getUsername())
                .claim("id", account.getId())
                .claim("role", account.getAccountRole())
                .claim("email", account.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key)
                .compact();
    }

    public static String validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // return username
    }
}
