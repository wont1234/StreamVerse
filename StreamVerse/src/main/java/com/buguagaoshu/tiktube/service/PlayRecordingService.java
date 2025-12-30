package com.buguagaoshu.tiktube.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.tiktube.entity.FileTableEntity;
import com.buguagaoshu.tiktube.entity.PlayRecordingEntity;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 鎾斁璁板綍
 *
 */
public interface PlayRecordingService extends IService<PlayRecordingEntity> {

    /**
     * 鑾峰彇鍘嗗彶璁板綍
     * */
    IPage<PlayRecordingEntity> queryPage(Map<String, Object> params, HttpServletRequest request);


    /**
     * 鏌ユ壘鎾斁璁板綍浣跨敤 articleId
     * */
    PlayRecordingEntity findPlayRecordingEntityByArticleIdAndVideoId(Long articleId, Long videoId, Long userId);

    /**
     * 淇濆瓨鎾斁璁板綍
     * @return ArticleId 闇€瑕佸鍔犳挱鏀鹃噺
     *         0 涓嶉渶瑕佸鍔?     * */
    long saveHistory(FileTableEntity file, Long userId, String ua);

    /**
     * 浠婃棩鎾斁鍘嗗彶璁板綍鍒楄〃
     * */
    List<PlayRecordingEntity> todayPlayList(Long userId);


    int todayPlayCount(Long userId);
}

