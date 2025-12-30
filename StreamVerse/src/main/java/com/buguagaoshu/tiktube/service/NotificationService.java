package com.buguagaoshu.tiktube.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.tiktube.enums.NotificationType;
import com.buguagaoshu.tiktube.utils.PageUtils;
import com.buguagaoshu.tiktube.entity.NotificationEntity;

import java.util.Map;

/**
 * 閫氱煡琛? *
 */
public interface NotificationService extends IService<NotificationEntity> {

    PageUtils queryPage(Map<String, Object> params);


    /**
     * 鍙戦€侀€氱煡
     * @param notifier 鍙戦€佷汉
     * @param receiver 鎺ユ敹浜?     * @param outerId 甯栧瓙ID
     * @param commentId 璇勮ID
     * @param linkMessage 閾炬帴娑堟伅
     * @param content 娑堟伅鍐呭
     * @param type 娑堟伅绫诲瀷
     * */
    void sendNotification(
            long notifier,
            long receiver,
            long outerId,
            long articleId,
            long commentId,
            String title,
            String linkMessage,
            String content,
            NotificationType type
    );


    /**
     * 鏌ヨ閫氱煡
     * */
    PageUtils queryNotification(Map<String, Object> params, Long userId);


    boolean readNotification(NotificationEntity notification, Long userId);


    boolean readAllNotification(Long userId);

    long countNotification(Long userId);
}

