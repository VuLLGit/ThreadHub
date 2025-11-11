package com.example.ThreadHub.util;

import com.example.ThreadHub.entity.Account;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {
    private static final String SECRET = "12345678987654321";
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
