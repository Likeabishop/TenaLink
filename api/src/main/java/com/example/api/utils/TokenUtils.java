package com.example.api.utils;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;

import com.example.api.entities.RefreshToken;
import com.example.api.entities.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TokenUtils {
    public String generateToken() {
        byte[] randomBytes = new byte[32]; // 256-bit token
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public LocalDateTime generateExpiryDate() {
        return LocalDateTime.now().plusMinutes(16);
    }

}
