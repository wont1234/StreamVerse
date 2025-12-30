package com.buguagaoshu.tiktube.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import com.buguagaoshu.tiktube.valid.ListValue;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * OSS閰嶇疆琛? * 
 */
@Data
@TableName("oss_config")
public class OSSConfigEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 閰嶇疆鍚嶇О
     */
    @Size(min = 1, max = 255)
    private String configName;

    /**
     * 瀛樺偍妗跺悕瀛?     */
    @Size(min = 1, max = 255)
    private String bucketName;

    /**
     * 绔偣
     */
    @Size(min = 1, max = 999)
    private String endpoint;

    /**
     * 璁块棶瀵嗛挜ID
     */
    @Size(min = 1, max = 999)
    private String accessKey;

    /**
     * 璁块棶瀵嗛挜瀵嗙爜
     */
    @Size(min = 1, max = 999)
    private String secretKey;

    /**
     * 鍦板尯
     */
    @Size(min = 1, max = 255)
    private String region;

    /**
     * 鑷畾涔夊煙鍚?     */
    @Size(min = 1, max = 255)
    private String urlPrefix;

    /**
     * Endpoint 璁块棶椋庢牸 0 path style锛?Virtual Hosted Style
     */
    @ListValue(value = {0,1})
    private Integer pathStyleAccess;

    /**
     * 鍏跺畠鍙傛暟閰嶇疆
     */
    private String other;

    /**
     * 鍒涘缓鏃堕棿
     */
    private Long createTime;

    /**
     * 0 鍏抽棴锛?鍚敤
     */
    @ListValue(value = {0,1})
    private Integer status;

    /**
     * 鍒涘缓浜?     */
    private Long creatorId;

    /**
     * 鏇存柊浜?     */
    private Long updaterId;

    /**
     * 鏇存柊鏃堕棿
     */
    private Long updateTime;
}