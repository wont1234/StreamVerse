package com.buguagaoshu.tiktube.controller;

import com.buguagaoshu.tiktube.entity.FavoritesLabelEntity;
import com.buguagaoshu.tiktube.service.FavoritesLabelService;
import com.buguagaoshu.tiktube.utils.JwtUtil;
import com.buguagaoshu.tiktube.vo.ResponseDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favorites/label")
public class FavoritesLabelController {

    private final FavoritesLabelService favoritesLabelService;

    @Autowired
    public FavoritesLabelController(FavoritesLabelService favoritesLabelService) {
        this.favoritesLabelService = favoritesLabelService;
    }

    // 获取收藏夹标签列表（若无则创建默认标签）
    @GetMapping("/list")
    public ResponseDetails list(HttpServletRequest request) {
        long userId = JwtUtil.getUserId(request);
        favoritesLabelService.getOrCreateDefaultLabelId(userId);
        List<FavoritesLabelEntity> list = favoritesLabelService.listByUser(userId);
        return ResponseDetails.ok().put("data", list);
    }

    // 新建收藏夹标签
    @PostMapping("/create")
    public ResponseDetails create(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        long userId = JwtUtil.getUserId(request);
        Object nameObj = body.get("name");
        String name = nameObj == null ? null : String.valueOf(nameObj).trim();
        if (name == null || name.isEmpty()) {
            return ResponseDetails.ok().put("data", null);
        }
        FavoritesLabelEntity created = favoritesLabelService.createLabel(userId, name);
        return ResponseDetails.ok().put("data", created);
    }

    // 删除收藏夹标签（会把已有收藏迁移到默认收藏夹）
    @PostMapping("/delete")
    public ResponseDetails delete(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        long userId = JwtUtil.getUserId(request);
        Object idObj = body.get("labelId");
        Long labelId = idObj == null ? null : Long.parseLong(String.valueOf(idObj));
        boolean ok = favoritesLabelService.deleteLabel(userId, labelId);
        return ResponseDetails.ok().put("data", ok);
    }
}
