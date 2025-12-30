package com.buguagaoshu.tiktube.service.impl;

import com.buguagaoshu.tiktube.cache.CountRecorder;
import com.buguagaoshu.tiktube.cache.WebSettingCache;
import com.buguagaoshu.tiktube.dto.ArtDanmakuDto;
import com.buguagaoshu.tiktube.entity.FileTableEntity;
import com.buguagaoshu.tiktube.enums.ArticleStatusEnum;
import com.buguagaoshu.tiktube.enums.ReturnCodeEnum;
import com.buguagaoshu.tiktube.enums.TypeCode;
import com.buguagaoshu.tiktube.exception.UserNotLoginException;
import com.buguagaoshu.tiktube.filter.TextFilter;
import com.buguagaoshu.tiktube.pipe.AiExaminePipe;
import com.buguagaoshu.tiktube.service.ArticleService;
import com.buguagaoshu.tiktube.service.FileTableService;
import com.buguagaoshu.tiktube.utils.*;
import com.buguagaoshu.tiktube.config.MyConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;


import com.buguagaoshu.tiktube.dao.DanmakuDao;
import com.buguagaoshu.tiktube.entity.DanmakuEntity;
import com.buguagaoshu.tiktube.service.DanmakuService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.transaction.annotation.Transactional;

/**
 * 弹幕服务实现类
 */
@Service("danmakuService")
public class DanmakuServiceImpl extends ServiceImpl<DanmakuDao, DanmakuEntity> implements DanmakuService {

    /**
     * Redis 弹幕频道前缀
     */
    private static final String DANMAKU_CHANNEL_PREFIX = "danmaku:video:";

    private final ArticleService articleService;

    private final FileTableService fileTableService;

    private final CountRecorder countRecorder;

    private final WebSettingCache webSettingCache;

    private final IpUtil ipUtil;

    private final AiExaminePipe aiExaminePipe;

    private final MyConfigProperties configProperties;

    @Autowired(required = false)
    private org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public DanmakuServiceImpl(ArticleService articleService,
                              FileTableService fileTableService,
                              CountRecorder countRecorder,
                              WebSettingCache webSettingCache,
                              IpUtil ipUtil, AiExaminePipe aiExaminePipe,
                              MyConfigProperties configProperties) {
        this.articleService = articleService;
        this.fileTableService = fileTableService;
        this.countRecorder = countRecorder;
        this.webSettingCache = webSettingCache;
        this.ipUtil = ipUtil;
        this.aiExaminePipe = aiExaminePipe;
        this.configProperties = configProperties;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<DanmakuEntity> page = this.page(
                new Query<DanmakuEntity>().getPage(params),
                new QueryWrapper<DanmakuEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * {
     *     text: '', // 弹幕文本
     *     time: 10, // 弹幕时间, 默认为当前播放器时间
     *     mode: 0, // 弹幕模式: 0: 滚动(默认)，1: 顶部，2: 底部
     *     color: '#FFFFFF', // 弹幕颜色，默认为白色
     *     border: false, // 弹幕是否有描边, 默认为 false
     *     style: {}, // 弹幕自定义样式, 默认为空对象
     * }
     * 区别在于 dplayer 的 mode 为 type
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReturnCodeEnum saveArtDanmaku(ArtDanmakuDto danmakuDto, HttpServletRequest request) {
        long userId = -1;
        try {
            userId = JwtUtil.getUserId(request);
        }catch (UserNotLoginException e) {
            return ReturnCodeEnum.NO_LOGIN;
        }

        // ArticleEntity video = articleService.getById(danmakuDto.getId());
        FileTableEntity fileTableEntity = fileTableService.getById(danmakuDto.getId());
        if (fileTableEntity == null) {
            return ReturnCodeEnum.NOO_FOUND;
        }
        if (fileTableEntity.getArticleId() == null) {
            return ReturnCodeEnum.NOO_FOUND;
        }

        DanmakuEntity danmakuEntity = new DanmakuEntity();
        danmakuEntity.setAuthor(userId);
        danmakuEntity.setCreateTime(System.currentTimeMillis());
        danmakuEntity.setVideoId(danmakuDto.getId());
        danmakuEntity.setText(danmakuDto.getText());
        danmakuEntity.setColor(danmakuDto.getColor());
        danmakuEntity.setTime(danmakuDto.getTime());
        danmakuEntity.setType(danmakuDto.getType());
        String ip = ipUtil.getIpAddr(request);
        danmakuEntity.setIp(ip);
        danmakuEntity.setUa(ipUtil.getUa(request));
        danmakuEntity.setCity(ipUtil.getCity(ip));
        // TODO 升级完成后这部分可以删除
        // 去掉开头的 # 符号
        String cleanHex = danmakuDto.getColor().replace("#", "");
        // 将十六进制字符串转换为十进制整数
        danmakuEntity.setColorDec(Long.parseLong(cleanHex, 16));
        
        // 先使用 Filter4J 自动检测：违规 -> 待审核，正常 -> 直接发布
        // 放宽规则：
        // 1. 只对「纯中文 + 长度 >= 6」的文本启用 Filter4J
        // 2. 混合字母/数字的文本不走 Filter4J（避免误判）
        String rawText = danmakuDto.getText();
        String trimmedText = rawText == null ? "" : rawText.trim();
        // 纯中文：只包含汉字和常见中文标点
        boolean isPureChinese = !trimmedText.isEmpty() && trimmedText.matches("[\\u4e00-\\u9fa5\\u3000-\\u303f\\uff00-\\uffef]+");
        boolean commonDaily = isPureChinese && trimmedText.matches(".*(今天|明天|后天|星期[一二三四五六日天]|周[一二三四五六日天]|天气).*");
        boolean needFilter = isPureChinese && trimmedText.length() >= 6 && !commonDaily;

        boolean filter4jIllegal = false;
        if (needFilter && TextFilter.isInitialized()) {
            filter4jIllegal = TextFilter.isIllegal(trimmedText);
        }

        if (filter4jIllegal) {
            // 文本被模型判为违规：进入待审核
            danmakuEntity.setStatus(TypeCode.EXAM);
            this.save(danmakuEntity);

            // 只有在后台开启弹幕审核 + 开启AI时，才额外走 AI 审核
            boolean openDanmakuExam = webSettingCache.getWebConfigData().getOpenDanmakuExam();
            if (openDanmakuExam && webSettingCache.getWebConfigData().getOpenAIConfig()) {
                aiExaminePipe.submitDanmaku(danmakuEntity);
            }

            countRecorder.recordDanmaku(fileTableEntity.getArticleId(), 1L);
            return ReturnCodeEnum.NEED_EXAM;
        }

        // Filter4J 判为正常内容：直接发布（不再被后台审核开关拦住）
        danmakuEntity.setStatus(TypeCode.NORMAL);
        this.save(danmakuEntity);
        countRecorder.recordDanmaku(fileTableEntity.getArticleId(), 1L);

        // 发布弹幕消息到 Redis，供 WebSocket 实时推送
        publishDanmakuToRedis(danmakuDto.getId(), danmakuEntity);

        return ReturnCodeEnum.SUCCESS;
    }

    @Override
    public PageUtils getAllDanmaku(Map<String, Object> params) {
        QueryWrapper<DanmakuEntity> wrapper = new QueryWrapper<>();
        String userId = (String) params.get("userId");
        String videoId = (String) params.get("videoId");
        String status = (String) params.get("status");

        if (userId != null) {
            wrapper.eq("author", userId);
        }
        if (videoId != null) {
            wrapper.eq("video_id", videoId);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("create_time");

        IPage<DanmakuEntity> page = this.page(
                new Query<DanmakuEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleDanmakuStatus(long id) {
        DanmakuEntity danmaku = this.getById(id);
        if (danmaku == null) {
            return false;
        }
        FileTableEntity fileTableEntity = fileTableService.getById(danmaku.getVideoId());
        // 切换状态：正常 <-> 删除
        if (danmaku.getStatus().equals(ArticleStatusEnum.DELETE.getCode())) {
            danmaku.setStatus(ArticleStatusEnum.NORMAL.getCode());
            articleService.addDanmakuCount(fileTableEntity.getArticleId(), 1L);
        } else {
            danmaku.setStatus(ArticleStatusEnum.DELETE.getCode());
            articleService.addDanmakuCount(fileTableEntity.getArticleId(), -1L);
        }

        this.updateById(danmaku);
        return true;
    }

    @Override
    public List<DanmakuEntity> artDanmakuList(Long id, Integer max) {
        Map<String, Object> params = new HashMap<>(2);
        params.put("limit", max);
        QueryWrapper<DanmakuEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("video_id", id);
        wrapper.eq("status", TypeCode.NORMAL);
        wrapper.orderByDesc("create_time");
        IPage<DanmakuEntity> page = this.page(
                new Query<DanmakuEntity>().getPage(params),
                wrapper
        );
        page.getRecords().parallelStream().forEach(d -> {
            d.setCity(null);
            d.setIp(null);
            d.setUa(null);
        });
        return page.getRecords();
    }

    /**
     * 发布弹幕消息到 Redis
     * 供 WebSocket 实时推送给其他观看同一视频的用户
     */
    private void publishDanmakuToRedis(Long videoId, DanmakuEntity danmaku) {
        if (redisTemplate == null || !configProperties.getOpenRedis()) {
            return;
        }
        try {
            String channel = DANMAKU_CHANNEL_PREFIX + videoId;
            redisTemplate.convertAndSend(channel, danmaku);
        } catch (Exception e) {
            // 发送失败不影响主流程
        }
    }
}