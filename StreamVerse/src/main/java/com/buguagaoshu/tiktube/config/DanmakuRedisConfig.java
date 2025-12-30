package com.buguagaoshu.tiktube.config;

import com.buguagaoshu.tiktube.websocket.DanmakuRedisSubscriber;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

/**
 * 弹幕 Redis 配置类
 * 用于配置 Redis 消息监听容器
 */
@Configuration
@ConditionalOnProperty(prefix = "tiktube", name = "open-redis", havingValue = "true")
public class DanmakuRedisConfig {

    /**
     * 配置 Redis 消息监听容器
     */
    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
                                                 DanmakuRedisSubscriber subscriber) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        
        // 订阅弹幕频道: danmaku:video:*
        container.addMessageListener(subscriber, new PatternTopic("danmaku:video:*"));
        
        return container;
    }
}
