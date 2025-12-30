package com.buguagaoshu.tiktube.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 收藏夹标签
 */
@Data
@TableName("favorites_label")
public class FavoritesLabelEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    private Long userId;

    private Long createTime;
}
