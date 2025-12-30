package com.buguagaoshu.tiktube.websocket;

import com.buguagaoshu.tiktube.entity.ChatMessageEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@Slf4j
@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;

    /**
     * key: userId, value: sessions
     */
    private final Map<Long, Set<WebSocketSession>> userSessions = new ConcurrentHashMap<>();

    public ChatWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = getUserId(session);
        if (userId == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }
        userSessions.computeIfAbsent(userId, k -> new CopyOnWriteArraySet<>()).add(session);
        log.info("Chat WebSocket connected: userId={}, sessionId={}, onlineSessions={}", userId, session.getId(), userSessions.get(userId).size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = getUserId(session);
        if (userId == null) {
            return;
        }
        Set<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                userSessions.remove(userId);
            }
        }
        log.info("Chat WebSocket closed: userId={}, sessionId={}, code={}", userId, session.getId(), status.getCode());
    }

    public void pushChatMessage(ChatMessageEntity msg) {
        if (msg == null) {
            return;
        }
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", "chat_message");
        payload.put("data", msg);

        sendToUser(msg.getReceiverId(), payload);
        // 同步给发送方（多端）
        sendToUser(msg.getSenderId(), payload);
    }

    private void sendToUser(Long userId, Map<String, Object> payload) {
        if (userId == null) {
            return;
        }
        Set<WebSocketSession> sessions = userSessions.get(userId);
        if (sessions == null || sessions.isEmpty()) {
            return;
        }
        try {
            String json = objectMapper.writeValueAsString(payload);
            TextMessage message = new TextMessage(json);
            for (WebSocketSession s : sessions) {
                if (s.isOpen()) {
                    try {
                        s.sendMessage(message);
                    } catch (IOException e) {
                        log.warn("Chat WebSocket send failed: userId={}, sessionId={}", userId, s.getId(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Chat WebSocket payload serialize failed", e);
        }
    }

    private Long getUserId(WebSocketSession session) {
        Object v = session.getAttributes().get("userId");
        if (v instanceof Long) {
            return (Long) v;
        }
        if (v instanceof Integer) {
            return ((Integer) v).longValue();
        }
        return null;
    }
}
