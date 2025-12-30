package com.buguagaoshu.tiktube.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

/**
 * WebSocket 配置类
 * 用于弹幕实时推送
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final DanmakuWebSocketHandler danmakuWebSocketHandler;
    private final DanmakuHandshakeInterceptor danmakuHandshakeInterceptor;

    private final ChatWebSocketHandler chatWebSocketHandler;
    private final ChatHandshakeInterceptor chatHandshakeInterceptor;

    public WebSocketConfig(DanmakuWebSocketHandler danmakuWebSocketHandler,
                           DanmakuHandshakeInterceptor danmakuHandshakeInterceptor,
                           ChatWebSocketHandler chatWebSocketHandler,
                           ChatHandshakeInterceptor chatHandshakeInterceptor) {
        this.danmakuWebSocketHandler = danmakuWebSocketHandler;
        this.danmakuHandshakeInterceptor = danmakuHandshakeInterceptor;
        this.chatWebSocketHandler = chatWebSocketHandler;
        this.chatHandshakeInterceptor = chatHandshakeInterceptor;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 注册弹幕 WebSocket 端点
        // 客户端连接地址: ws://host:port/ws/danmaku/{videoId}
        registry.addHandler(danmakuWebSocketHandler, "/ws/danmaku/*")
                .addInterceptors(danmakuHandshakeInterceptor)
                .setAllowedOriginPatterns("*"); // Spring Boot 3 推荐使用 AllowedOriginPatterns

        // 注册私信 WebSocket 端点
        // 客户端连接地址: ws://host:port/ws/chat
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
                .addInterceptors(chatHandshakeInterceptor)
                .setAllowedOriginPatterns("*");
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(8192);
        container.setMaxBinaryMessageBufferSize(8192);
        return container;
    }
}
