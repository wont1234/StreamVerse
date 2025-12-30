package com.buguagaoshu.tiktube.vo;

import lombok.Data;

@Data
public class ChatSendRequest {
    private Long toUserId;
    private String content;
}
