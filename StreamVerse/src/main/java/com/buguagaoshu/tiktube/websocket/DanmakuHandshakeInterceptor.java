package com.buguagaoshu.tiktube.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * 弹幕 WebSocket 握手拦截器
 * 用于在建立连接前提取视频ID和用户信息
 */
@Slf4j
@Component
public class DanmakuHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            String path = request.getURI().getPath();
            // 路径格式: /ws/danmaku/{videoId}
            String[] parts = path.split("/");
            if (parts.length > 0) {
                try {
                    String videoIdStr = parts[parts.length - 1];
                    Long videoId = Long.parseLong(videoIdStr);
                    // 将 videoId 放入 WebSocketSession 的属性中
                    attributes.put("videoId", videoId);
                    log.info("WebSocket 握手成功: videoId={}", videoId);
                    return true;
                } catch (NumberFormatException e) {
                    log.warn("WebSocket 握手失败: 无效的视频ID, path={}", path);
                }
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 握手后的处理
    }
}
