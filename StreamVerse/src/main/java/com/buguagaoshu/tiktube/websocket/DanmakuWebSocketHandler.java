package com.buguagaoshu.tiktube.websocket;

import com.buguagaoshu.tiktube.entity.DanmakuEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 弹幕 WebSocket 处理器
 * 管理客户端连接，按视频ID分组
 */
@Slf4j
@Component
public class DanmakuWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper;

    /**
     * 存储每个视频的 WebSocket 会话
     * key: videoId, value: 该视频的所有 WebSocket 会话
     */
    private final Map<Long, Set<WebSocketSession>> videoSessions = new ConcurrentHashMap<>();

    public DanmakuWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long videoId = getVideoId(session);
        if (videoId != null) {
            videoSessions.computeIfAbsent(videoId, k -> new CopyOnWriteArraySet<>()).add(session);
            log.info("WebSocket 连接建立成功: videoId={}, sessionId={}, 当前在线人数={}", 
                    videoId, session.getId(), getOnlineCount(videoId));
        } else {
            log.warn("WebSocket 连接建立失败: 未能获取 videoId, sessionId={}, uri={}", session.getId(), session.getUri());
            session.close(CloseStatus.BAD_DATA);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long videoId = getVideoId(session);
        if (videoId != null) {
            Set<WebSocketSession> sessions = videoSessions.get(videoId);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    videoSessions.remove(videoId);
                }
            }
            log.info("WebSocket 连接关闭: videoId={}, sessionId={}, code={}, reason={}", 
                    videoId, session.getId(), status.getCode(), status.getReason());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 客户端发送的消息（可用于心跳检测等）
    }

    /**
     * 向指定视频的所有客户端广播弹幕
     */
    public void broadcastDanmaku(Long videoId, DanmakuEntity danmaku) {
        Set<WebSocketSession> sessions = videoSessions.get(videoId);
        int onlineCount = sessions == null ? 0 : sessions.size();
        
        if (sessions == null || sessions.isEmpty()) {
            log.info("广播弹幕跳过: videoId={} 无在线连接, 弹幕内容={}", videoId, danmaku.getText());
            return;
        }

        log.info("开始广播弹幕: videoId={}, 在线人数={}, 弹幕内容={}", videoId, onlineCount, danmaku.getText());

        try {
            String json = objectMapper.writeValueAsString(danmaku);
            TextMessage message = new TextMessage(json);

            int successCount = 0;
            for (WebSocketSession session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.sendMessage(message);
                        successCount++;
                    } catch (IOException e) {
                        log.warn("发送给客户端失败: sessionId={}", session.getId(), e);
                    }
                }
            }
            log.info("广播完成: videoId={}, 成功发送 {}/{}", videoId, successCount, onlineCount);
        } catch (Exception e) {
            log.error("序列化弹幕消息失败", e);
        }
    }

    /**
     * 从 Session 属性中获取 videoId (由 Interceptor 放入)
     */
    private Long getVideoId(WebSocketSession session) {
        Object videoId = session.getAttributes().get("videoId");
        if (videoId instanceof Long) {
            return (Long) videoId;
        }
        return null;
    }

    /**
     * 获取当前在线观看某视频的用户数
     */
    public int getOnlineCount(Long videoId) {
        Set<WebSocketSession> sessions = videoSessions.get(videoId);
        return sessions == null ? 0 : sessions.size();
    }
}
