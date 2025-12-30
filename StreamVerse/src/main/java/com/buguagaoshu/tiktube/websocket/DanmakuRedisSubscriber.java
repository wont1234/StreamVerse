package com.buguagaoshu.tiktube.websocket;

import com.buguagaoshu.tiktube.entity.DanmakuEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Redis 弹幕消息订阅器
 * 接收 Redis 频道消息并通过 WebSocket 广播给客户端
 */
@Slf4j
@Component
public class DanmakuRedisSubscriber implements MessageListener {

    /**
     * Redis 弹幕频道前缀
     */
    public static final String DANMAKU_CHANNEL_PREFIX = "danmaku:video:";

    private final DanmakuWebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;

    public DanmakuRedisSubscriber(DanmakuWebSocketHandler webSocketHandler, ObjectMapper objectMapper) {
        this.webSocketHandler = webSocketHandler;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
            String body = new String(message.getBody(), StandardCharsets.UTF_8);

            // 解析视频ID：danmaku:video:{videoId}
            if (channel.startsWith(DANMAKU_CHANNEL_PREFIX)) {
                String videoIdStr = channel.substring(DANMAKU_CHANNEL_PREFIX.length());
                Long videoId = Long.parseLong(videoIdStr);

                // 解析弹幕实体
                DanmakuEntity danmaku = objectMapper.readValue(body, DanmakuEntity.class);

                // 通过 WebSocket 广播给所有观看该视频的客户端
                webSocketHandler.broadcastDanmaku(videoId, danmaku);

                log.debug("Redis 收到弹幕并广播: videoId={}, text={}", videoId, danmaku.getText());
            }
        } catch (Exception e) {
            log.error("处理 Redis 弹幕消息失败", e);
        }
    }
}
