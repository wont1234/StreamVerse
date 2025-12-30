package com.buguagaoshu.tiktube.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 收藏表
 */
@Data
@TableName("favorites_table")
public class FavoritesTableEntity {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     *
     */
    private Long articeId;

    /**
     *
     */
    private Long userId;

    private Long favoritesLabelId;

    /**
     *
     */
    private Long createTime;
}
