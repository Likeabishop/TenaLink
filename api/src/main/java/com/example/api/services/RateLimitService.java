package com.example.api.services;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.api.exceptions.RateLimitExceededException;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RateLimitService {
    
    @Value("${bucket4j.login-attempts:5}")
    private int loginAttempts;

    @Value("${bucket4j.login-window:900}")
    private int loginWindow;

    @Value("${bucket4j.otp-requests:3}")
    private int otpRequests;

    @Value("${bucket4j.otp-window:300}")
    private int otpWindow;

    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> otpBuckets = new ConcurrentHashMap<>();

    public void checkRateLimit(String key) {
        Bucket bucket = getBucketForKey(key);

        if (!bucket.tryConsume(1)) {
            log.warn("Rate limit exceeded for key: {}", key);
            throw new RateLimitExceededException("Too many requests. Please try again later.");
        }
    }

    private Bucket getBucketForKey(String key) {
        if (key.startsWith("LOGIN_") || key.startsWith("REGISTER_") || key.startsWith("PASSWORD_RESET")) {
            return loginBuckets.computeIfAbsent(key, k -> createLoginBucket());
        } else {
            return otpBuckets.computeIfAbsent(key, k -> createOtpBucket());
        }
    }

    private Bucket createOtpBucket() {
        Bandwidth limit = Bandwidth.classic(
            otpRequests, 
            Refill.greedy(otpRequests, Duration.ofSeconds(otpWindow)));
            return Bucket.builder().addLimit(limit).build();
    }

    private Bucket createLoginBucket() {
        Bandwidth limit = Bandwidth.classic(
            loginAttempts, 
            Refill.greedy(loginAttempts, Duration.ofSeconds(loginWindow)));
            return Bucket.builder().addLimit(limit).build();
    }
    
}
