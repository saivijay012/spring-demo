package com.vijay.demo.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {

    // Optional: This is required if the SECRET is not provided to fetch custom generated key
    /*@Autowired
    private SecretKeyProvider secretKeyProvider;
     */

    // Secret key for signing JWT
    public static final String SECRET = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";

    // Extract username from JWT token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract expiration date from the token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract any claim (like username, expiration, etc.) from the token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Parse all claims from the token using Jwts.parser()
    private Claims extractAllClaims(String token) {
        // Create JwtParser using JwtParserBuilder
        return Jwts.parser()
                .setSigningKey(getSignKey()) // Set the signing key
                .build()                    // Build the JwtParser
                .parseClaimsJws(token)
                .getBody();
    }

    // Check if the token has expired
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date(System.currentTimeMillis()));
    }

    // Validate the token by comparing the username and expiration date
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // Generate a new token for a user
    public String generateToken(String userName){
        Map<String,Object> claims = new HashMap<>();
        return createToken(claims,userName);
    }

    // Create the token with claims and subject (username)
    public String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims) // Set custom claims
                .setSubject(subject) // Set the username or user ID as the subject
                .setIssuedAt(new Date(System.currentTimeMillis())) // Issue time
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Token expiration (1 hour)
                .signWith(getSignKey(), SignatureAlgorithm.HS256) // Signing key and algorithm
                .compact(); // Build and compact the token
    }

    // Retrieve the signing key from the secret
    private Key getSignKey() {
//        byte[] keyBytes= Decoders.BASE64.decode(secretKeyProvider.getSecret()); // Optional: Key retrieved from Autowired SecretKeyProvider class
        byte[] keyBytes= Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
