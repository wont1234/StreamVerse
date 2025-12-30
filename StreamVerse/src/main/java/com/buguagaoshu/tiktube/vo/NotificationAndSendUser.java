package com.buguagaoshu.tiktube.vo;

import com.buguagaoshu.tiktube.entity.NotificationEntity;
import com.buguagaoshu.tiktube.entity.UserEntity;
import lombok.Data;

/**
 *
 * @create 2025-04-19
 * 消息通知和发送通知的人
 */
@Data
public class NotificationAndSendUser {
    private NotificationEntity notification;

    private UserEntity user;
}
