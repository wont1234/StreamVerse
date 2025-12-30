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

    @GetMapping("/list")
    public ResponseDetails list(HttpServletRequest request) {
        long userId = JwtUtil.getUserId(request);
        favoritesLabelService.getOrCreateDefaultLabelId(userId);
        List<FavoritesLabelEntity> list = favoritesLabelService.listByUser(userId);
        return ResponseDetails.ok().put("data", list);
    }

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
}
