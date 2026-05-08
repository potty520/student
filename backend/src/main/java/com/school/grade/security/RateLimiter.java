package com.school.grade.security;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Simple in-memory rate limiter for login attempts.
 * Tracks attempts per IP address within a sliding time window.
 */
public class RateLimiter {

    private final int maxAttempts;
    private final long windowMs;
    private final ConcurrentHashMap<String, Window> attempts = new ConcurrentHashMap<>();

    public RateLimiter(int maxAttempts, long windowMs) {
        this.maxAttempts = maxAttempts;
        this.windowMs = windowMs;
    }

    /**
     * Record an attempt for the given key. Returns true if allowed, false if rate-limited.
     */
    public synchronized boolean tryAcquire(String key) {
        long now = System.currentTimeMillis();
        Window w = attempts.get(key);

        if (w == null || now - w.windowStart > windowMs) {
            w = new Window(now, 0);
            attempts.put(key, w);
        }

        w.count++;
        return w.count <= maxAttempts;
    }

    /**
     * Reset attempts for a key (e.g. after successful login).
     */
    public void reset(String key) {
        attempts.remove(key);
    }

    /**
     * Clean up expired entries periodically.
     */
    public void cleanup() {
        long now = System.currentTimeMillis();
        attempts.entrySet().removeIf(e -> now - e.getValue().windowStart > windowMs);
    }

    public static RateLimiter loginRateLimiter() {
        // 10 attempts per minute per IP
        return new RateLimiter(10, TimeUnit.MINUTES.toMillis(1));
    }

    private static class Window {
        final long windowStart;
        int count;

        Window(long windowStart, int count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}