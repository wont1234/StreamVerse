package com.buguagaoshu.tiktube.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.buguagaoshu.tiktube.entity.FollowEntity;
import com.buguagaoshu.tiktube.entity.UserEntity;
import com.buguagaoshu.tiktube.service.ArticleService;
import com.buguagaoshu.tiktube.service.FollowService;
import com.buguagaoshu.tiktube.service.UserService;
import com.buguagaoshu.tiktube.utils.JwtUtil;
import com.buguagaoshu.tiktube.vo.CreatorStatsVo;
import com.buguagaoshu.tiktube.vo.FansProfileVo;
import com.buguagaoshu.tiktube.vo.ResponseDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 创作者中心控制器
 */
@RestController
@RequestMapping("/api/studio")
public class CreatorController {

    private final ArticleService articleService;
    private final FollowService followService;
    private final UserService userService;

    @Autowired
    public CreatorController(ArticleService articleService, FollowService followService, UserService userService) {
        this.articleService = articleService;
        this.followService = followService;
        this.userService = userService;
    }

    /**
     * 获取创作者统计数据
     */
    @GetMapping("/stats")
    public ResponseDetails getCreatorStats(HttpServletRequest request) {
        long userId = JwtUtil.getUserId(request);
        CreatorStatsVo stats = articleService.getCreatorStats(userId);

        // 统计真实粉丝数：follow_user = 当前用户
        long fansCount = followService.count(
                new QueryWrapper<FollowEntity>().eq("follow_user", userId)
        );
        stats.setTotalFansCount(fansCount);

        return ResponseDetails.ok().put("data", stats);
    }

    /**
     * 获取粉丝画像数据
     */
    @GetMapping("/fans/profile")
    public ResponseDetails getFansProfile(HttpServletRequest request) {
        long userId = JwtUtil.getUserId(request);
        FansProfileVo profile = new FansProfileVo();

        // 1. 获取所有粉丝 (关注了当前用户的人)
        List<FollowEntity> fansList = followService.list(
                new QueryWrapper<FollowEntity>().eq("follow_user", userId)
        );
        long totalFans = fansList.size();
        profile.setTotalFans(totalFans);

        if (totalFans == 0) {
            profile.setMutualFans(0L);
            profile.setMutualRate(0.0);
            profile.setActiveCreators(0L);
            profile.setActiveCreatorRate(0.0);
            profile.setHighFansFans(0L);
            profile.setNewFans(0L);
            profile.setNewFans30d(0L);
            profile.setNewFansTrend7d(Collections.emptyList());
            profile.setFansCountBuckets(new LinkedHashMap<>());
            profile.setNewUserFans(0L);
            profile.setNewUserFansRate(0.0);
            profile.setHighFansRate(0.0);
            return ResponseDetails.ok().put("data", profile);
        }

        // 提取粉丝用户ID
        Set<Long> fansUserIds = fansList.stream()
                .map(FollowEntity::getCreateUser)
                .collect(Collectors.toSet());

        // 2. 统计互关粉丝数 (当前用户也关注了这些粉丝)
        long mutualFans = followService.count(
                new QueryWrapper<FollowEntity>()
                        .eq("create_user", userId)
                        .in("follow_user", fansUserIds)
        );
        profile.setMutualFans(mutualFans);
        profile.setMutualRate(Math.round(mutualFans * 100.0 / totalFans * 10) / 10.0);

        // 3. 获取粉丝用户详细信息
        List<UserEntity> fansUsers = userService.listByIds(fansUserIds);

        // 4. 统计活跃创作者 (有投稿的粉丝)
        long activeCreators = fansUsers.stream()
                .filter(u -> u.getSubmitCount() != null && u.getSubmitCount() > 0)
                .count();
        profile.setActiveCreators(activeCreators);
        profile.setActiveCreatorRate(Math.round(activeCreators * 100.0 / totalFans * 10) / 10.0);

        // 5. 统计高粉丝量粉丝 (粉丝数 >= 100)
        long highFansFans = fansUsers.stream()
                .filter(u -> u.getFansCount() != null && u.getFansCount() >= 100)
                .count();
        profile.setHighFansFans(highFansFans);

        profile.setHighFansRate(Math.round(highFansFans * 100.0 / totalFans * 10) / 10.0);

        // 6. 统计新粉丝 (7天内关注)
        long sevenDaysAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L;
        long newFans = fansList.stream()
                .filter(f -> f.getCreateTime() != null && f.getCreateTime() > sevenDaysAgo)
                .count();
        profile.setNewFans(newFans);

        // 7. 统计新粉丝 (30天内关注)
        long thirtyDaysAgo = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000L;
        long newFans30d = fansList.stream()
                .filter(f -> f.getCreateTime() != null && f.getCreateTime() > thirtyDaysAgo)
                .count();
        profile.setNewFans30d(newFans30d);

        // 8. 近7天每日新增粉丝趋势
        ZoneId zoneId = ZoneId.systemDefault();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");
        LocalDate today = LocalDate.now(zoneId);
        List<FansProfileVo.NewFansTrendPoint> trend7d = java.util.stream.IntStream.rangeClosed(0, 6)
                .mapToObj(i -> {
                    LocalDate day = today.minusDays(6L - i);
                    long start = day.atStartOfDay(zoneId).toInstant().toEpochMilli();
                    long end = day.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli();
                    long count = fansList.stream()
                            .filter(f -> f.getCreateTime() != null && f.getCreateTime() >= start && f.getCreateTime() < end)
                            .count();
                    FansProfileVo.NewFansTrendPoint point = new FansProfileVo.NewFansTrendPoint();
                    point.setDay(day.format(formatter));
                    point.setCount(count);
                    return point;
                })
                .collect(Collectors.toList());
        profile.setNewFansTrend7d(trend7d);

        // 9. 粉丝影响力分桶 (按粉丝的粉丝数分层)
        Map<String, Long> buckets = new LinkedHashMap<>();
        buckets.put("0-9", 0L);
        buckets.put("10-99", 0L);
        buckets.put("100-999", 0L);
        buckets.put("1000+", 0L);
        for (UserEntity u : fansUsers) {
            long fansCount = u.getFansCount() == null ? 0L : u.getFansCount();
            if (fansCount <= 9) {
                buckets.put("0-9", buckets.get("0-9") + 1);
            } else if (fansCount <= 99) {
                buckets.put("10-99", buckets.get("10-99") + 1);
            } else if (fansCount <= 999) {
                buckets.put("100-999", buckets.get("100-999") + 1);
            } else {
                buckets.put("1000+", buckets.get("1000+") + 1);
            }
        }
        profile.setFansCountBuckets(buckets);

        // 10. 新注册粉丝 (粉丝账号注册时间在30天内)
        long newUserFans = fansUsers.stream()
                .filter(u -> u.getCreateTime() != null && u.getCreateTime() > thirtyDaysAgo)
                .count();
        profile.setNewUserFans(newUserFans);
        profile.setNewUserFansRate(Math.round(newUserFans * 100.0 / totalFans * 10) / 10.0);

        return ResponseDetails.ok().put("data", profile);
    }
}
