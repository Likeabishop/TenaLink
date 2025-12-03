package com.example.api.exceptions;

public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException(String message) { super(message); }
}
