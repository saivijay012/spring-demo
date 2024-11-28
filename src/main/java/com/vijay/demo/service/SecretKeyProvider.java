package com.vijay.demo.service;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.security.Key;

// Optional approach if the SECRET is not provided: This class will generate the SECRET, ensuring the key remains consistent throughout the application's runtime.

@Component
public class SecretKeyProvider {

    private String secret; // Base64-encoded key

    @PostConstruct
    public void init() {
        // Generate a secure HS256 key once and store it as Base64
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        this.secret = Encoders.BASE64.encode(key.getEncoded());
    }

    public String getSecret() {
        return secret;
    }
}

