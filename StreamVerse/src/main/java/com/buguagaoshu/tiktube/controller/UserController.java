package com.buguagaoshu.tiktube.controller;

import com.buguagaoshu.tiktube.config.WebConstant;
import com.buguagaoshu.tiktube.dto.LoginDetails;
import com.buguagaoshu.tiktube.dto.PasswordDto;
import com.buguagaoshu.tiktube.entity.UserEntity;
import com.buguagaoshu.tiktube.entity.UserRoleEntity;
import com.buguagaoshu.tiktube.enums.ExamineTypeEnum;
import com.buguagaoshu.tiktube.enums.NotificationType;
import com.buguagaoshu.tiktube.enums.ReturnCodeEnum;
import com.buguagaoshu.tiktube.enums.RoleTypeEnum;
import com.buguagaoshu.tiktube.enums.TypeCode;
import com.buguagaoshu.tiktube.service.NotificationService;
import com.buguagaoshu.tiktube.service.UserRoleService;
import com.buguagaoshu.tiktube.service.UserService;
import com.buguagaoshu.tiktube.utils.JwtUtil;
import com.buguagaoshu.tiktube.utils.MyStringUtils;
import com.buguagaoshu.tiktube.utils.PageUtils;
import com.buguagaoshu.tiktube.vo.AdminAddUserData;
import com.buguagaoshu.tiktube.vo.ResponseDetails;
import com.buguagaoshu.tiktube.vo.TOTPLoginKey;
import com.buguagaoshu.tiktube.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.WebUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

/**
 *
 * create          2020-09-05 14:48
 */
@RestController
public class UserController {
    private final UserService userService;

    private final NotificationService notificationService;

    private final UserRoleService userRoleService;

    @Autowired
    public UserController(UserService userService, NotificationService notificationService, UserRoleService userRoleService) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.userRoleService = userRoleService;
    }


    @PostMapping("/api/login")
    public ResponseDetails login(@Valid @RequestBody LoginDetails loginDetails,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        return ResponseDetails.ok().put("data", userService.login(loginDetails, request, response));
    }

    @PostMapping("/api/login/totp")
    public ResponseDetails loginTOTP(@RequestBody TOTPLoginKey totpLoginKey,
                                     HttpServletResponse response,
                                     HttpServletRequest request) {
        return ResponseDetails.ok().put("data", userService.loginTOTP(totpLoginKey, request, response));
    }


    @PostMapping("/api/register")
    public ResponseDetails register(@Valid @RequestBody UserEntity userEntity, HttpServletRequest request) {
        return ResponseDetails.ok(userService.register(userEntity, request));
    }

    @PostMapping("/api/logout")
    public ResponseDetails logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = WebUtils.getCookie(request, WebConstant.COOKIE_TOKEN);
        if (cookie == null) {
            return ResponseDetails.ok(0, "没有登录");
        }
        cookie.setValue(null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseDetails.ok(200, "退出成功！");
    }

    @GetMapping("/api/user/info/{id}")
    public ResponseDetails userInfo(@PathVariable("id") Long userId) {
        return ResponseDetails.ok().put("data", userService.userInfo(userId));
    }

    @PostMapping("/api/user/forgot/password")
    public ResponseDetails forgotPassword(@RequestBody UserEntity user, HttpServletRequest request) {
        return ResponseDetails.ok().put("data", userService.forgotPassword(user, request.getSession().getId()));
    }

    @PostMapping("/api/user/update/avatar")
    public ResponseDetails updateUserAvatar(@Valid @RequestBody User user,
                                            HttpServletRequest request) {
        return ResponseDetails.ok(userService.updateAvatar(user, request));
    }

    @PostMapping("/api/user/update/top")
    public ResponseDetails updateTopImage(@Valid @RequestBody User user,
                                          HttpServletRequest request) {
        return ResponseDetails.ok(userService.updateTopImage(user, request));
    }

    @PostMapping("/api/user/update/password")
    public ResponseDetails updatePassword(@Valid @RequestBody PasswordDto passwordDto,
                                          HttpServletRequest request,
                                          HttpServletResponse response) {
        return ResponseDetails.ok(userService.updatePassword(passwordDto, request, response));
    }


    @PostMapping("/api/user/update/info")
    public ResponseDetails updateInfo(@Valid @RequestBody User user,
                                      HttpServletRequest request) {
        return ResponseDetails.ok(userService.updateInfo(user, request));
    }

    @GetMapping("/api/user/list/search")
    public ResponseDetails searchUser(@RequestParam Map<String, Object> params) {
        String search = (String) params.get("search");
        if (search != null && !search.isEmpty()) {
            PageUtils pageUtils = userService.userList(params);
            List<User> list = (List<User>) pageUtils.getList();
            list.forEach(u -> u.setMail(""));
            return ResponseDetails.ok().put("data", pageUtils);
        }
        return ResponseDetails.ok();
    }


    /**
     * 管理员读取用户列表
     */
    @GetMapping("/api/admin/user/list")
    public ResponseDetails userList(@RequestParam Map<String, Object> params) {
        return ResponseDetails.ok().put("data", userService.userList(params));
    }

    @PostMapping("/api/admin/user/update/role")
    public ResponseDetails updateUserRole(@RequestBody UserRoleEntity userRole,
                                          HttpServletRequest request) {
        UserRoleEntity oldRole = userRoleService.findByUserId(userRole.getUserid());
        String oldRoleValue = oldRole == null ? null : oldRole.getRole();
        UserRoleEntity role;
        try {
            role = userService.updateRole(userRole, request);
        } catch (Exception e) {
            return ResponseDetails.ok(1006, e.getMessage() == null ? "没有权限" : e.getMessage());
        }

        if (role == null) {
            if (RoleTypeEnum.ADMIN.getRole().equals(userRole.getRole())) {
                return ResponseDetails.ok(1006, "权限不足：仅超级管理员可任命/撤销管理员");
            }
            return ResponseDetails.ok(1006, "没有权限");
        }

        if (role.getRole().equals(RoleTypeEnum.VIP.getRole())) {
            notificationService.sendNotification(
                    JwtUtil.getUserId(request),
                    role.getUserid(),
                    -1,
                    -1,
                    -1,
                    "恭喜你，已经成为尊贵的 VPI 用户",
                    "",
                    "管理员已经将你设置为 VPI 用户，有效期为："
                            + MyStringUtils.formatTime(userRole.getVipStartTime())
                            + " - "
                            + MyStringUtils.formatTime(userRole.getVipStopTime()),
                    NotificationType.SYSTEM
            );
        }

        if (role.getRole().equals(RoleTypeEnum.ADMIN.getRole())) {
            notificationService.sendNotification(
                    0,
                    role.getUserid(),
                    -1,
                    -1,
                    -1,
                    "你已被任命为管理员",
                    "",
                    "你的账号已被设置为管理员，现在可以访问管理后台并执行管理操作",
                    NotificationType.SYSTEM
            );
        }

        boolean oldIsAdmin = oldRoleValue != null && RoleTypeEnum.ADMIN.getRole().equals(oldRoleValue);
        boolean newIsAdmin = RoleTypeEnum.ADMIN.getRole().equals(role.getRole());
        if (oldIsAdmin && !newIsAdmin) {
            notificationService.sendNotification(
                    0,
                    role.getUserid(),
                    -1,
                    -1,
                    -1,
                    "你的管理员权限已被取消",
                    "",
                    "你的账号已被取消管理员权限，如有疑问请联系超级管理员",
                    NotificationType.SYSTEM
            );
        }

        return ResponseDetails.ok().put("data", role);
    }

    @PostMapping("/api/admin/user/update/pwd")
    public ResponseDetails resetPassword(@RequestBody UserEntity userEntity) {
        return ResponseDetails.ok().put("data", userService.resetPassword(userEntity));
    }

    @PostMapping("/api/admin/user/add")
    public ResponseDetails adminAddUser(@RequestBody AdminAddUserData adminAddUserData,
                                        HttpServletRequest request) {
        return ResponseDetails.ok(userService.addUser(adminAddUserData, request));
    }


    /**
     * 封禁用户
     * */
    @PostMapping("/api/admin/user/lock")
    public ResponseDetails lockUser(@RequestBody UserEntity userEntity, HttpServletRequest request) {
        long adminUserId = JwtUtil.getUserId(request);
        boolean b = userService.lockUser(userEntity, adminUserId);
        String title = "账号已被管理员解封！";
        String content = "由于您近期表现良好，管理员已提前解封您的账号！";
        if (userEntity.getStatus().equals(TypeCode.USER_LOCK)) {
            title = "账号被封禁！";
            if (userEntity.getBlockEndTime().equals(0L)) {
                content = "由于您近期多次违反社区规定，账号已被永久封禁！";
            } else {
                content = "由于您近期多次违反社区规定，账号已被封禁到：" + MyStringUtils.formatTime(userEntity.getBlockEndTime());
            }
        }
        if (b) {
            notificationService.sendNotification(
                    adminUserId,
                    userEntity.getId(),
                    -1,
                    -1,
                    -1,
                    title,
                    "",
                    content,
                    NotificationType.SYSTEM
            );
        }

        return ResponseDetails.ok().put("data", b);
    }
}
