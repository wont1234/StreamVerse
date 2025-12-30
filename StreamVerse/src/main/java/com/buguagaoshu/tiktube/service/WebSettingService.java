package com.buguagaoshu.tiktube.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.tiktube.enums.ReturnCodeEnum;
import com.buguagaoshu.tiktube.utils.PageUtils;
import com.buguagaoshu.tiktube.entity.WebSettingEntity;

import java.util.Map;

/**
 * 
 *
 * @deprecated 宸茬粡杩佺Щ鑷?WebConfigService 瀹炵幇
 */
@Deprecated
public interface WebSettingService extends IService<WebSettingEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 鑾峰彇鏈€鏂扮殑web璁剧疆
     *
     * @return 璁剧疆
     */
    WebSettingEntity getNewSetting();


    ReturnCodeEnum saveSetting(WebSettingEntity webSettingEntity);
}

