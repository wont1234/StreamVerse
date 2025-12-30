package com.buguagaoshu.tiktube.config;

import com.buguagaoshu.tiktube.enums.RoleTypeEnum;
import com.buguagaoshu.tiktube.dao.UserDao;
import com.buguagaoshu.tiktube.entity.UserEntity;
import com.buguagaoshu.tiktube.entity.UserRoleEntity;
import com.buguagaoshu.tiktube.service.UserRoleService;
import com.buguagaoshu.tiktube.utils.IpUtil;
import com.buguagaoshu.tiktube.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import java.io.PrintWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;



/**
 *
 * create          2020-09-08 17:05
 */
@Service
@Slf4j
public class AdminInterceptor implements HandlerInterceptor {
    private final IpUtil ipUtil;

    private final UserDao userDao;

    private final UserRoleService userRoleService;

    private static final String SUPER_ADMIN_MAIL = "123@qq.com";

    @Autowired
    public AdminInterceptor(IpUtil ipUtil, UserDao userDao, UserRoleService userRoleService) {
        this.ipUtil = ipUtil;
        this.userDao = userDao;
        this.userRoleService = userRoleService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Claims claims = JwtUtil.getUser(request);
        if (claims == null) {
            log.warn("访问 ip 为 {} 访问管理员接口被拦截", ipUtil.getIpAddr(request));
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write("{\"status\":0,\"message\":\"登录过期或未登录\"}");
            writer.flush();
            return false;
        }
        if (SUPER_ADMIN_MAIL.equals(claims.getSubject())) {
            return true;
        }
        try {
            UserEntity user = userDao.selectById(Long.parseLong(claims.getId()));
            if (user != null && user.getMail() != null && SUPER_ADMIN_MAIL.equals(user.getMail())) {
                return true;
            }
        } catch (Exception ignored) {
        }

        // 以数据库当前角色为准，避免用户被授予管理员后因 JWT 未刷新仍被拦截
        try {
            UserRoleEntity roleEntity = userRoleService.findByUserId(Long.parseLong(claims.getId()));
            if (roleEntity != null) {
                // 数据库中已存在角色记录，则以数据库为准（防止撤销管理员后仍被旧 JWT 放行）
                return RoleTypeEnum.ADMIN.getRole().equals(roleEntity.getRole());
            }
        } catch (Exception ignored) {
        }

        if (RoleTypeEnum.ADMIN.getRole().equals(claims.get("authorities"))) {
            return true;
        }
        log.warn("用户id为 {} 的用户访问管理员接口被拦截，访问 ip 为 {}", claims.getId(), ipUtil.getIpAddr(request));
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.write("{\"status\":403,\"message\":\"权限不足\"}");
        writer.flush();
        return false;
    }
}
