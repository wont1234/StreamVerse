package com.buguagaoshu.tiktube.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.tiktube.entity.FavoritesLabelEntity;

import java.util.List;

public interface FavoritesLabelService extends IService<FavoritesLabelEntity> {

    Long getOrCreateDefaultLabelId(Long userId);

    List<FavoritesLabelEntity> listByUser(Long userId);

    FavoritesLabelEntity createLabel(Long userId, String name);
}
