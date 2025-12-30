package com.buguagaoshu.tiktube.service.impl;

import com.buguagaoshu.tiktube.cache.CountRecorder;
import com.buguagaoshu.tiktube.cache.PlayCountRecorder;
import com.buguagaoshu.tiktube.entity.ArticleEntity;
import com.buguagaoshu.tiktube.enums.ArticleStatusEnum;
import com.buguagaoshu.tiktube.enums.ExamineTypeEnum;
import com.buguagaoshu.tiktube.service.ArticleService;
import com.buguagaoshu.tiktube.service.FavoritesLabelService;
import com.buguagaoshu.tiktube.vo.FavoritesAndArticle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.tiktube.utils.PageUtils;
import com.buguagaoshu.tiktube.utils.Query;

import com.buguagaoshu.tiktube.dao.FavoritesTableDao;
import com.buguagaoshu.tiktube.entity.FavoritesTableEntity;
import com.buguagaoshu.tiktube.service.FavoritesTableService;
import org.springframework.transaction.annotation.Transactional;


@Service("favoritesTableService")
public class FavoritesTableServiceImpl extends ServiceImpl<FavoritesTableDao, FavoritesTableEntity> implements FavoritesTableService {

    final ArticleService articleService;

    final CountRecorder countRecorder;

    final PlayCountRecorder playCountRecorder;

    final FavoritesLabelService favoritesLabelService;

    @Autowired
    public FavoritesTableServiceImpl(ArticleService articleService,
                                     CountRecorder countRecorder,
                                     PlayCountRecorder playCountRecorder,
                                     FavoritesLabelService favoritesLabelService) {
        this.articleService = articleService;
        this.countRecorder = countRecorder;
        this.playCountRecorder = playCountRecorder;
        this.favoritesLabelService = favoritesLabelService;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<FavoritesTableEntity> page = this.page(
                new Query<FavoritesTableEntity>().getPage(params),
                new QueryWrapper<FavoritesTableEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FavoritesTableEntity toggleFavorites(FavoritesTableEntity favorite, Long userId) {
        Long labelId = favorite.getFavoritesLabelId();
        if (labelId == null) {
            labelId = favoritesLabelService.getOrCreateDefaultLabelId(userId);
        }

        QueryWrapper<FavoritesTableEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("artice_id", favorite.getArticeId());
        FavoritesTableEntity one = this.getOne(wrapper);
        // 没有收藏过，增加收藏
        if (one == null) {
            FavoritesTableEntity favoritesTableEntity = new FavoritesTableEntity();
            favoritesTableEntity.setUserId(userId);
            favoritesTableEntity.setArticeId(favorite.getArticeId());
            favoritesTableEntity.setCreateTime(System.currentTimeMillis());
            favoritesTableEntity.setFavoritesLabelId(labelId);
            this.save(favoritesTableEntity);
            countRecorder.recordArticleFavorite(favorite.getArticeId(), 1);
            // articleService.addCount("favorite_count", favorite.getArticeId(), 1);
            return favoritesTableEntity;
        } else {
            // A 模式：一个视频只能在一个收藏夹
            // 如果当前收藏夹一致：取消收藏
            if (one.getFavoritesLabelId() != null && one.getFavoritesLabelId().equals(labelId)) {
                this.removeById(one);
                countRecorder.recordArticleFavorite(favorite.getArticeId(), -1);
                // articleService.addCount("favorite_count", one.getArticeId(), -1);
                return favorite;
            }
            // 收藏夹不同：移动到目标收藏夹（不取消收藏）
            one.setFavoritesLabelId(labelId);
            one.setCreateTime(System.currentTimeMillis());
            this.updateById(one);
            return one;
        }
    }

    @Override
    public boolean checkFavorites(Long articleId, Long userId) {
        QueryWrapper<FavoritesTableEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("artice_id",articleId);
        FavoritesTableEntity one = this.getOne(wrapper);
        return one != null;
    }

    @Override
    public PageUtils userFavorites(Map<String, Object> params, Long userId) {
        QueryWrapper<FavoritesTableEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);

        Object labelIdObj = params.get("labelId");
        if (labelIdObj != null) {
            try {
                Long labelId = Long.parseLong(String.valueOf(labelIdObj));
                wrapper.eq("favorites_label_id", labelId);
            } catch (Exception ignored) {
            }
        }

        wrapper.orderByDesc("create_time");
        IPage<FavoritesTableEntity> page = this.page(
                new Query<FavoritesTableEntity>().getPage(params),
                wrapper
        );
        if (page.getTotal() == 0) {
            return new PageUtils(page);
        }
        Set<Long> articleIds = page.getRecords().stream().map(FavoritesTableEntity::getArticeId).collect(Collectors.toSet());
        List<ArticleEntity> articleEntities = articleService.listByIds(articleIds);
        Map<Long, ArticleEntity> map = new HashMap<>();
        for (ArticleEntity articleEntity : articleEntities) {
            articleEntity.setUa("");
            articleEntity.setIp("");
            articleEntity.setCity("");
            articleEntity.setExamineUser(null);
            articleEntity.setExamineMessage("");
            if (articleEntity.getExamineStatus().equals(ExamineTypeEnum.SUCCESS.getCode()) && articleEntity.getStatus().equals(ArticleStatusEnum.NORMAL.getCode())) {
                //articleEntity.setViewCount(articleEntity.getViewCount() + countRecorder.geta);

                countRecorder.syncArticleCount(articleEntity);
                articleEntity.setViewCount(articleEntity.getViewCount() + playCountRecorder.getPlayCount(articleEntity.getId()));
                map.put(articleEntity.getId(), articleEntity);
            }
        }
        List<FavoritesAndArticle> favoritesAndArticleList = new ArrayList<>();
        for (FavoritesTableEntity favoritesTableEntity : page.getRecords()) {
            FavoritesAndArticle favoritesAndArticle = new FavoritesAndArticle();
            favoritesAndArticle.setFavorite(favoritesTableEntity);
            favoritesAndArticle.setArticleEntity(map.get(favoritesTableEntity.getArticeId()));
            favoritesAndArticleList.add(favoritesAndArticle);
        }
        return new PageUtils(favoritesAndArticleList, page.getTotal(), page.getSize(), page.getCurrent());
    }

}