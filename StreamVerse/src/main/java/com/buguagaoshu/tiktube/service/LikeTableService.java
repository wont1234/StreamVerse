package com.buguagaoshu.tiktube.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.tiktube.utils.PageUtils;
import com.buguagaoshu.tiktube.entity.LikeTableEntity;

import java.util.Map;

/**
 * 鐐硅禐
 *
 */
public interface LikeTableService extends IService<LikeTableEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * type: 銆? 瑙嗛鍥剧墖鏂囩珷涓诲笘瀛? 1 璇勮銆?     * */
    Map<String, Object> toggleLike(Long likeObjId, Integer type, Long userId);


    LikeTableEntity checkLike(Long likeObjId, Integer type, Long userId);
}

