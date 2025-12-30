package com.buguagaoshu.tiktube.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.tiktube.utils.PageUtils;
import com.buguagaoshu.tiktube.entity.DislikeTableEntity;

import java.util.Map;

/**
 * 鐐硅俯
 *
 */
public interface DislikeTableService extends IService<DislikeTableEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * type: 銆? 甯栧瓙瑙嗛鍥剧墖锛?1 璇勮銆?     */
    Map<String, Object> toggleDislike(Long dislikeObjId, Integer type, Long userId);

    DislikeTableEntity checkDislike(Long dislikeObjId, Integer type, Long userId);
}

