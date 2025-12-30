package com.buguagaoshu.tiktube.websocket;

import com.buguagaoshu.tiktube.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Slf4j
@Component
public class ChatHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (!(request instanceof ServletServerHttpRequest ssr)) {
            return false;
        }

        HttpServletRequest servletRequest = ssr.getServletRequest();
        Claims user = JwtUtil.getUser(servletRequest);
        if (user == null || user.getId() == null) {
            log.info("Chat WebSocket handshake rejected: not logged in");
            return false;
        }

        try {
            long userId = Long.parseLong(user.getId());
            attributes.put("userId", userId);
            return true;
        } catch (Exception e) {
            log.warn("Chat WebSocket handshake rejected: invalid userId", e);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // no-op
    }
}
