package com.rozgar.backend.chat.config;

import com.rozgar.backend.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) {

        String query = request.getURI().getQuery();
        if (query == null) {
            log.warn("WebSocket connection rejected — no query params");
            return false;
        }

        // Parse token from ?token=xxx
        String token = null;
        for (String param : query.split("&")) {
            if (param.startsWith("token=")) {
                token = param.substring(6);
                break;
            }
        }

        if (token == null || token.isBlank()) {
            log.warn("WebSocket connection rejected — no token in query");
            return false;
        }

        try {
            String email = jwtService.extractEmail(token);
            if (email != null) {
                attributes.put("email", email);
                log.debug("WebSocket handshake approved for: {}", email);
                return true;
            }
        } catch (Exception e) {
            log.warn("WebSocket connection rejected — invalid token: {}", e.getMessage());
        }

        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request,
                               ServerHttpResponse response,
                               WebSocketHandler wsHandler,
                               Exception ex) {
    }
}