package com.buguagaoshu.tiktube.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.tiktube.entity.ArticleEntity;
import com.buguagaoshu.tiktube.utils.PageUtils;
import com.buguagaoshu.tiktube.entity.CommentEntity;
import com.buguagaoshu.tiktube.vo.CommentVo;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

/**
 * 
 *
 */
public interface CommentService extends IService<CommentEntity> {

    PageUtils queryPage(Map<String, Object> params, HttpServletRequest request);

    /**
     * 淇濆瓨璇勮
     * */
    CommentEntity saveComment(CommentVo commentVo, HttpServletRequest request);


    /**
     * 鑾峰彇绋夸欢璇勮鍒楄〃
     * */
    PageUtils commentList(Map<String, Object> params, long articleId, long fatherId, int type, Integer sort);


    /**
     * 灞曠ず鎵€鏈夎瘎璁哄垪琛?     * */
    PageUtils getAllComment(Map<String, Object> params);


    /**
     * 鍒囨崲璇勮鐘舵€?     * */
    boolean toggleCommentStatus(long id);


    boolean hasUserCommentInArticle(long id, long userId);



    void addCount(String col, long commentId, long count);


    /**
     * 鍙戦€佽瘎璁洪€氱煡
     * */
    void send(CommentEntity commentEntity,
              CommentEntity fatherComment,
              ArticleEntity articleEntity);
}

