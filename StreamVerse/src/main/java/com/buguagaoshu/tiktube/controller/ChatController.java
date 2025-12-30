package com.buguagaoshu.tiktube.controller;

import com.buguagaoshu.tiktube.enums.ReturnCodeEnum;
import com.buguagaoshu.tiktube.service.ChatService;
import com.buguagaoshu.tiktube.utils.JwtUtil;
import com.buguagaoshu.tiktube.vo.ResponseDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/send")
    public ResponseDetails send(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        long fromUserId;
        try {
            fromUserId = JwtUtil.getUserId(request);
        } catch (Exception e) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_LOGIN);
        }

        Long toUserId = body.get("toUserId") == null ? null : Long.parseLong(String.valueOf(body.get("toUserId")));
        Integer msgType = body.get("msgType") == null ? null : Integer.parseInt(String.valueOf(body.get("msgType")));
        String content = body.get("content") == null ? null : String.valueOf(body.get("content"));

        Map<String, Object> res = chatService.send(fromUserId, toUserId, msgType, content);
        return ResponseDetails.ok()
                .put("status", res.get("status"))
                .put("message", res.get("message"))
                .put("data", res.get("data"));
    }

    @GetMapping("/conversations")
    public ResponseDetails conversations(HttpServletRequest request) {
        long userId;
        try {
            userId = JwtUtil.getUserId(request);
        } catch (Exception e) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_LOGIN);
        }
        Map<String, Object> res = chatService.listConversations(userId);
        return ResponseDetails.ok().put("data", res.get("data"));
    }

    @GetMapping("/messages")
    public ResponseDetails messages(@RequestParam Long withUserId,
                                   @RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer limit,
                                   HttpServletRequest request) {
        long userId;
        try {
            userId = JwtUtil.getUserId(request);
        } catch (Exception e) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_LOGIN);
        }
        Map<String, Object> res = chatService.listMessages(userId, withUserId, page, limit);
        return ResponseDetails.ok()
                .put("data", res.get("data"))
                .put("total", res.get("total"));
    }

    @GetMapping("/unreadCount")
    public ResponseDetails unreadCount(HttpServletRequest request) {
        long userId;
        try {
            userId = JwtUtil.getUserId(request);
        } catch (Exception e) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_LOGIN);
        }
        Map<String, Object> res = chatService.unreadCount(userId);
        return ResponseDetails.ok().put("data", res.get("data"));
    }

    @PostMapping("/read")
    public ResponseDetails markRead(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        long userId;
        try {
            userId = JwtUtil.getUserId(request);
        } catch (Exception e) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_LOGIN);
        }
        Long withUserId = body.get("withUserId") == null ? null : Long.parseLong(String.valueOf(body.get("withUserId")));
        Map<String, Object> res = chatService.markRead(userId, withUserId);
        return ResponseDetails.ok()
                .put("status", res.get("status"))
                .put("message", res.get("message"))
                .put("data", res.get("data"));
    }

    @GetMapping("/unreadMessages")
    public ResponseDetails unreadMessages(@RequestParam(required = false) Integer limit, HttpServletRequest request) {
        long userId;
        try {
            userId = JwtUtil.getUserId(request);
        } catch (Exception e) {
            return ResponseDetails.ok(ReturnCodeEnum.NO_LOGIN);
        }
        Map<String, Object> res = chatService.listUnreadMessages(userId, limit);
        return ResponseDetails.ok().put("data", res.get("data"));
    }
}
