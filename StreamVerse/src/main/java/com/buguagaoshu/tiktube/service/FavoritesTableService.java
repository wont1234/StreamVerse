package com.buguagaoshu.tiktube.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.tiktube.utils.PageUtils;
import com.buguagaoshu.tiktube.entity.FavoritesTableEntity;

import java.util.Map;

/**
 * 鏀惰棌澶? *
 */
public interface FavoritesTableService extends IService<FavoritesTableEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 鏂板鏀惰棌
     * TODO 鏀惰棌澶瑰姛鑳?     * */
    FavoritesTableEntity toggleFavorites(FavoritesTableEntity favorite, Long userId);


    /**
     * 妫€鏌ュ綋鍓嶇浠舵槸鍚﹁鏀惰棌
     * */
    boolean checkFavorites(Long articleId, Long userId);


    /**
     * 鐢ㄦ埛鏀惰棌鍒楄〃
     * */
    PageUtils userFavorites(Map<String, Object> params, Long userId);
}

