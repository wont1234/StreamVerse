package com.buguagaoshu.tiktube.vo;

import lombok.Data;

/**
 * 创作者统计数据
 */
@Data
public class CreatorStatsVo {
    /**
     * 总播放量
     */
    private Long totalViewCount;

    /**
     * 总点赞数
     */
    private Long totalLikeCount;

    /**
     * 总评论数
     */
    private Long totalCommentCount;

    /**
     * 总收藏数
     */
    private Long totalFavoriteCount;

    /**
     * 总弹幕数
     */
    private Long totalDanmakuCount;

    /**
     * 总粉丝数
     */
    private Long totalFansCount;

    /**
     * 总稿件数
     */
    private Long totalArticleCount;

    /**
     * 待审核稿件数
     */
    private Long pendingExamineCount;

    /**
     * 今日播放量
     */
    private Long todayViewCount;

    /**
     * 今日新增粉丝
     */
    private Long todayFansCount;
}
