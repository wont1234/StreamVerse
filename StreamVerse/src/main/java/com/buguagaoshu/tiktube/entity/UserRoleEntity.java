package com.buguagaoshu.tiktube.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;


import lombok.Data;

/**
 *
 *
 */
@Data
@TableName("user_role")
public class UserRoleEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     *
     */
    private Long userid;

    /**
     * 瑙掕壊
     */
    private String role;

    /**
     *
     */
    private Long createTime;

    /**
     *
     */
    private Long updateTime;

    /**
     *
     */
    private Long vipStartTime;

    /**
     *
     */
    private Long vipStopTime;


    /**
     * 淇敼浜?     * 鍒濆鎯呭喌涓?0锛屼唬琛ㄧ郴缁?     * */
    private Long modified;

}
