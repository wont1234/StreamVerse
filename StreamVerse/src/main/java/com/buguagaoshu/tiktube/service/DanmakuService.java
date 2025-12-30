package com.buguagaoshu.tiktube.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.tiktube.dto.ArtDanmakuDto;
import com.buguagaoshu.tiktube.enums.ReturnCodeEnum;
import com.buguagaoshu.tiktube.utils.PageUtils;
import com.buguagaoshu.tiktube.entity.DanmakuEntity;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 寮瑰箷琛? *
 */
public interface DanmakuService extends IService<DanmakuEntity> {

    PageUtils queryPage(Map<String, Object> params);


    ReturnCodeEnum saveArtDanmaku(ArtDanmakuDto danmakuDto, HttpServletRequest request);


    /**
     * 鑾峰彇鎵€鏈夊脊骞曞垪琛紙绠＄悊鍛樻帴鍙ｏ級
     */
    PageUtils getAllDanmaku(Map<String, Object> params);

    /**
     * 鍒囨崲寮瑰箷鐘舵€侊紙绠＄悊鍛樻帴鍙ｏ級
     */
    boolean toggleDanmakuStatus(long id);


    List<DanmakuEntity> artDanmakuList(Long id, Integer max);
}

