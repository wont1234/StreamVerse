package com.buguagaoshu.tiktube.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 閰嶇疆璁剧疆琛? * 
 */
@Data
@TableName("web_config")
public class WebConfigEntity {
    /**
     * 涓婚敭ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 閰嶇疆json鏂囦欢
     */
    private String jsonText;

    /**
     * 鍒涘缓鐢ㄦ埛
     */
    private Long createUser;

    /**
     * 鍒涘缓鏃堕棿
     */
    private Long createTime;

    /**
     * ip
     */
    private String ip;
}