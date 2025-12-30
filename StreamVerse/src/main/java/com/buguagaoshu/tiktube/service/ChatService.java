package com.buguagaoshu.tiktube.service;

import com.buguagaoshu.tiktube.entity.ChatMessageEntity;

import java.util.Map;

public interface ChatService {

    Map<String, Object> send(Long fromUserId, Long toUserId, Integer msgType, String content);

    Map<String, Object> listConversations(Long userId);

    Map<String, Object> listMessages(Long userId, Long withUserId, Integer page, Integer limit);

    Map<String, Object> unreadCount(Long userId);

    Map<String, Object> markRead(Long userId, Long withUserId);

    Map<String, Object> listUnreadMessages(Long userId, Integer limit);
}
