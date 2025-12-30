package com.buguagaoshu.tiktube.vo;

import com.buguagaoshu.tiktube.entity.ArticleEntity;
import com.buguagaoshu.tiktube.entity.FavoritesTableEntity;
import lombok.Data;

/**
 *
 * @create 2025-04-08
 */
@Data
public class FavoritesAndArticle {
    private FavoritesTableEntity favorite;

    private ArticleEntity articleEntity;
}
