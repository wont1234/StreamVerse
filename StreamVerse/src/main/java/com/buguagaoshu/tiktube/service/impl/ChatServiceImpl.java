package com.buguagaoshu.tiktube.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.buguagaoshu.tiktube.dao.ChatConversationDao;
import com.buguagaoshu.tiktube.dao.ChatMessageDao;
import com.buguagaoshu.tiktube.entity.ChatConversationEntity;
import com.buguagaoshu.tiktube.entity.ChatMessageEntity;
import com.buguagaoshu.tiktube.entity.UserEntity;
import com.buguagaoshu.tiktube.enums.ReturnCodeEnum;
import com.buguagaoshu.tiktube.exception.UserNotFoundException;
import com.buguagaoshu.tiktube.service.ChatService;
import com.buguagaoshu.tiktube.service.FollowService;
import com.buguagaoshu.tiktube.service.UserService;
import com.buguagaoshu.tiktube.websocket.ChatWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ChatServiceImpl implements ChatService {

    private final ChatConversationDao chatConversationDao;
    private final ChatMessageDao chatMessageDao;
    private final FollowService followService;
    private final UserService userService;

    private final ChatWebSocketHandler chatWebSocketHandler;

    @Autowired
    public ChatServiceImpl(ChatConversationDao chatConversationDao,
                           ChatMessageDao chatMessageDao,
                           FollowService followService,
                           UserService userService,
                           ChatWebSocketHandler chatWebSocketHandler) {
        this.chatConversationDao = chatConversationDao;
        this.chatMessageDao = chatMessageDao;
        this.followService = followService;
        this.userService = userService;
        this.chatWebSocketHandler = chatWebSocketHandler;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> send(Long fromUserId, Long toUserId, Integer msgType, String content) {
        if (msgType == null) {
            msgType = 0;
        }
        if (msgType != 0 && msgType != 1) {
            return Map.of("status", ReturnCodeEnum.DATA_VALID_EXCEPTION.getCode(), "message", "不支持的消息类型");
        }
        if (toUserId == null || content == null || content.trim().isEmpty()) {
            return Map.of("status", ReturnCodeEnum.DATA_VALID_EXCEPTION.getCode(), "message", "消息内容不能为空");
        }
        if (content.length() > 1000) {
            return Map.of("status", ReturnCodeEnum.DATA_VALID_EXCEPTION.getCode(), "message", "消息内容过长");
        }
        if (Objects.equals(fromUserId, toUserId)) {
            return Map.of("status", ReturnCodeEnum.DATA_VALID_EXCEPTION.getCode(), "message", "不能给自己发送私信");
        }

        UserEntity toUser = userService.getById(toUserId);
        if (toUser == null) {
            throw new UserNotFoundException("用户不存在");
        }

        // 互关判定：双方都关注才算互关
        boolean aFollowB = followService.checkFollow(toUserId, fromUserId);
        boolean bFollowA = followService.checkFollow(fromUserId, toUserId);
        boolean mutual = aFollowB && bFollowA;

        if (!mutual) {
            // 未互关：同一对用户 A->B 只能发 1 条
            Long sentCount = chatMessageDao.selectCount(new QueryWrapper<ChatMessageEntity>()
                    .eq("sender_id", fromUserId)
                    .eq("receiver_id", toUserId));
            if (sentCount != null && sentCount >= 1) {
                return Map.of("status", ReturnCodeEnum.NO_POWER.getCode(), "message", "未互相关注前只能发送一条私信");
            }
        }

        long u1 = Math.min(fromUserId, toUserId);
        long u2 = Math.max(fromUserId, toUserId);
        ChatConversationEntity conv = chatConversationDao.selectOne(new QueryWrapper<ChatConversationEntity>()
                .eq("user1_id", u1)
                .eq("user2_id", u2));

        long now = System.currentTimeMillis();
        if (conv == null) {
            conv = new ChatConversationEntity();
            conv.setUser1Id(u1);
            conv.setUser2Id(u2);
            conv.setCreateTime(now);
            chatConversationDao.insert(conv);
        }

        ChatMessageEntity msg = new ChatMessageEntity();
        msg.setConversationId(conv.getId());
        msg.setSenderId(fromUserId);
        msg.setReceiverId(toUserId);
        msg.setMsgType(msgType);
        msg.setContent(content.trim());
        msg.setCreateTime(now);
        msg.setReadStatus(0);
        chatMessageDao.insert(msg);

        conv.setLastMessageId(msg.getId());
        conv.setLastMessageTime(now);
        chatConversationDao.updateById(conv);

        // WebSocket 实时推送（接收方 + 发送方多端同步）
        chatWebSocketHandler.pushChatMessage(msg);

        return Map.of("status", ReturnCodeEnum.SUCCESS.getCode(), "message", ReturnCodeEnum.SUCCESS.getMsg(), "data", msg);
    }

    @Override
    public Map<String, Object> listConversations(Long userId) {
        List<ChatConversationEntity> list = chatConversationDao.selectList(new QueryWrapper<ChatConversationEntity>()
                .and(w -> w.eq("user1_id", userId).or().eq("user2_id", userId))
                .orderByDesc("last_message_time"));

        List<Map<String, Object>> data = new ArrayList<>();
        for (ChatConversationEntity c : list) {
            Long otherId = Objects.equals(c.getUser1Id(), userId) ? c.getUser2Id() : c.getUser1Id();
            UserEntity u = userService.getById(otherId);
            if (u != null) {
                u.clean();
            }
            Map<String, Object> row = new HashMap<>();
            row.put("conversation", c);
            row.put("otherUser", u);
            if (c.getLastMessageId() != null) {
                ChatMessageEntity last = chatMessageDao.selectById(c.getLastMessageId());
                row.put("lastMessage", last);
            }

            // 未读数：对当前用户而言，统计对方发给我且未读的消息
            Long unread = chatMessageDao.selectCount(new QueryWrapper<ChatMessageEntity>()
                    .eq("conversation_id", c.getId())
                    .eq("receiver_id", userId)
                    .eq("read_status", 0));
            row.put("unreadCount", unread == null ? 0L : unread);
            data.add(row);
        }

        return Map.of("status", ReturnCodeEnum.SUCCESS.getCode(), "message", ReturnCodeEnum.SUCCESS.getMsg(), "data", data);
    }

    @Override
    public Map<String, Object> unreadCount(Long userId) {
        Long cnt = chatMessageDao.selectCount(new QueryWrapper<ChatMessageEntity>()
                .eq("receiver_id", userId)
                .eq("read_status", 0));
        return Map.of("status", ReturnCodeEnum.SUCCESS.getCode(), "message", ReturnCodeEnum.SUCCESS.getMsg(), "data", cnt == null ? 0L : cnt);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> markRead(Long userId, Long withUserId) {
        if (withUserId == null) {
            return Map.of("status", ReturnCodeEnum.DATA_VALID_EXCEPTION.getCode(), "message", "缺少withUserId", "data", false);
        }
        long u1 = Math.min(userId, withUserId);
        long u2 = Math.max(userId, withUserId);
        ChatConversationEntity conv = chatConversationDao.selectOne(new QueryWrapper<ChatConversationEntity>()
                .eq("user1_id", u1)
                .eq("user2_id", u2));
        if (conv == null) {
            return Map.of("status", ReturnCodeEnum.SUCCESS.getCode(), "message", ReturnCodeEnum.SUCCESS.getMsg(), "data", true);
        }

        ChatMessageEntity upd = new ChatMessageEntity();
        upd.setReadStatus(1);
        chatMessageDao.update(upd, new QueryWrapper<ChatMessageEntity>()
                .eq("conversation_id", conv.getId())
                .eq("sender_id", withUserId)
                .eq("receiver_id", userId)
                .eq("read_status", 0));

        return Map.of("status", ReturnCodeEnum.SUCCESS.getCode(), "message", ReturnCodeEnum.SUCCESS.getMsg(), "data", true);
    }

    @Override
    public Map<String, Object> listUnreadMessages(Long userId, Integer limit) {
        if (limit == null || limit < 1 || limit > 50) {
            limit = 20;
        }

        List<ChatMessageEntity> msgs = chatMessageDao.selectList(new QueryWrapper<ChatMessageEntity>()
                .eq("receiver_id", userId)
                .eq("read_status", 0)
                .orderByDesc("create_time")
                .last("limit " + limit));

        List<Map<String, Object>> data = new ArrayList<>();
        for (ChatMessageEntity m : msgs) {
            UserEntity u = userService.getById(m.getSenderId());
            if (u != null) {
                u.clean();
            }
            Map<String, Object> row = new HashMap<>();
            row.put("message", m);
            row.put("fromUser", u);
            data.add(row);
        }

        return Map.of("status", ReturnCodeEnum.SUCCESS.getCode(), "message", ReturnCodeEnum.SUCCESS.getMsg(), "data", data);
    }

    @Override
    public Map<String, Object> listMessages(Long userId, Long withUserId, Integer page, Integer limit) {
        if (withUserId == null) {
            return Map.of("status", ReturnCodeEnum.DATA_VALID_EXCEPTION.getCode(), "message", "缺少withUserId");
        }
        if (page == null || page < 1) {
            page = 1;
        }
        if (limit == null || limit < 1 || limit > 50) {
            limit = 20;
        }

        long u1 = Math.min(userId, withUserId);
        long u2 = Math.max(userId, withUserId);
        ChatConversationEntity conv = chatConversationDao.selectOne(new QueryWrapper<ChatConversationEntity>()
                .eq("user1_id", u1)
                .eq("user2_id", u2));
        if (conv == null) {
            return Map.of("status", ReturnCodeEnum.SUCCESS.getCode(), "message", ReturnCodeEnum.SUCCESS.getMsg(), "data", List.of());
        }

        IPage<ChatMessageEntity> p = chatMessageDao.selectPage(new Page<>(page, limit),
                new QueryWrapper<ChatMessageEntity>()
                        .eq("conversation_id", conv.getId())
                        .orderByDesc("create_time"));

        // 拉取会话消息后，将对方发给我的未读消息标记已读（不影响分页结果）
        ChatMessageEntity upd = new ChatMessageEntity();
        upd.setReadStatus(1);
        chatMessageDao.update(upd, new QueryWrapper<ChatMessageEntity>()
                .eq("conversation_id", conv.getId())
                .eq("sender_id", withUserId)
                .eq("receiver_id", userId)
                .eq("read_status", 0));

        return Map.of("status", ReturnCodeEnum.SUCCESS.getCode(), "message", ReturnCodeEnum.SUCCESS.getMsg(), "data", p.getRecords(), "total", p.getTotal());
    }
}
