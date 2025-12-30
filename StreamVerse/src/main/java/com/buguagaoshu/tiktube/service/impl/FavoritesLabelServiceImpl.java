package com.buguagaoshu.tiktube.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buguagaoshu.tiktube.dao.FavoritesLabelDao;
import com.buguagaoshu.tiktube.entity.FavoritesLabelEntity;
import com.buguagaoshu.tiktube.service.FavoritesLabelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("favoritesLabelService")
public class FavoritesLabelServiceImpl extends ServiceImpl<FavoritesLabelDao, FavoritesLabelEntity> implements FavoritesLabelService {

    private static final String DEFAULT_LABEL_NAME = "默认收藏夹";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long getOrCreateDefaultLabelId(Long userId) {
        FavoritesLabelEntity one = this.getOne(new QueryWrapper<FavoritesLabelEntity>()
                .eq("user_id", userId)
                .eq("name", DEFAULT_LABEL_NAME)
        );
        if (one != null) {
            return one.getId();
        }
        FavoritesLabelEntity created = new FavoritesLabelEntity();
        created.setUserId(userId);
        created.setName(DEFAULT_LABEL_NAME);
        created.setCreateTime(System.currentTimeMillis());
        this.save(created);
        return created.getId();
    }

    @Override
    public List<FavoritesLabelEntity> listByUser(Long userId) {
        return this.list(new QueryWrapper<FavoritesLabelEntity>()
                .eq("user_id", userId)
                .orderByAsc("id")
        );
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FavoritesLabelEntity createLabel(Long userId, String name) {
        FavoritesLabelEntity entity = new FavoritesLabelEntity();
        entity.setUserId(userId);
        entity.setName(name);
        entity.setCreateTime(System.currentTimeMillis());
        this.save(entity);
        return entity;
    }
}
