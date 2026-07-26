package com.rozgar.backend.auth.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@Order(1)
public class RateLimitFilter extends OncePerRequestFilter {

    // One bucket per IP address — stored in memory
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // 10 requests per minute per IP on auth endpoints
    private Bucket createBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(10,
                        Refill.greedy(10, Duration.ofMinutes(1))))
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        String uri = request.getRequestURI();

        // Only rate limit auth endpoints
        if (uri.startsWith("/api/v1/auth/")) {
            String ip = getClientIp(request);
            Bucket bucket = buckets.computeIfAbsent(ip, k -> createBucket());

            if (!bucket.tryConsume(1)) {
                log.warn("Rate limit exceeded for IP: {}", ip);
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"success\":false,\"message\":\"Too many requests. Please wait a minute before trying again.\",\"errorCode\":\"RATE_LIMIT_EXCEEDED\"}"
                );
                return;
            }
        }

        chain.doFilter(request, response);
    }

    // Handles proxies (Render puts real IP in X-Forwarded-For)
    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}