package com.buguagaoshu.tiktube.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 粉丝画像 VO
 */
@Data
public class FansProfileVo {
    
    /**
     * 粉丝总数
     */
    private Long totalFans;
    
    /**
     * 互关粉丝数
     */
    private Long mutualFans;
    
    /**
     * 互关率 (百分比)
     */
    private Double mutualRate;
    
    /**
     * 活跃创作者数量 (有投稿的粉丝)
     */
    private Long activeCreators;
    
    /**
     * 活跃创作者比例 (百分比)
     */
    private Double activeCreatorRate;
    
    /**
     * 高粉丝量粉丝数 (粉丝数 >= 100)
     */
    private Long highFansFans;
    
    /**
     * 新粉丝数 (7天内关注)
     */
    private Long newFans;

    /**
     * 新粉丝数 (30天内关注)
     */
    private Long newFans30d;

    /**
     * 近7天每日新增粉丝趋势
     */
    private List<NewFansTrendPoint> newFansTrend7d;

    /**
     * 粉丝影响力分桶 (以粉丝的粉丝数 fansCount 分层)
     */
    private Map<String, Long> fansCountBuckets;

    /**
     * 新注册粉丝数 (粉丝账号注册时间在30天内)
     */
    private Long newUserFans;

    /**
     * 新注册粉丝占比 (百分比)
     */
    private Double newUserFansRate;

    /**
     * 高粉丝量粉丝占比 (百分比)
     */
    private Double highFansRate;

    @Data
    public static class NewFansTrendPoint {
        private String day;
        private Long count;
    }
}
