package com.buguagaoshu.tiktube.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buguagaoshu.tiktube.dto.LoginDetails;
import com.buguagaoshu.tiktube.dto.PasswordDto;
import com.buguagaoshu.tiktube.entity.UserRoleEntity;
import com.buguagaoshu.tiktube.enums.ReturnCodeEnum;
import com.buguagaoshu.tiktube.utils.PageUtils;
import com.buguagaoshu.tiktube.entity.UserEntity;
import com.buguagaoshu.tiktube.vo.AdminAddUserData;
import com.buguagaoshu.tiktube.vo.TOTPLoginKey;
import com.buguagaoshu.tiktube.vo.TwoFactorData;
import com.buguagaoshu.tiktube.vo.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import java.util.Map;
import java.util.Set;

/**
 * 
 *
 */
public interface UserService extends IService<UserEntity> {

    PageUtils queryPage(Map<String, Object> params);


    PageUtils userList(Map<String, Object> params);

    /**
     * 鐧诲綍鎺ュ彛
     * @param loginDetails 鐧诲綍璇锋眰鏁版嵁
     * @param request 璇锋眰
     * @param response 灏?token 鍐欏叆 cookie
     * @return 鐢ㄦ埛鐧诲綍淇℃伅
     * */
    User login(LoginDetails loginDetails, HttpServletRequest request, HttpServletResponse response);



    /**
     * TOTP 涓ゆ璁よ瘉鎺ュ彛
     * */
    User loginTOTP(TOTPLoginKey userTotpLogin, HttpServletRequest request, HttpServletResponse response);

    /**
     * 娉ㄥ唽鎺ュ彛
     * @param userEntity 鐢ㄦ埛淇℃伅
     * @param request 璇锋眰淇℃伅
     * @return 娉ㄥ唽缁撴灉
     * */
    ReturnCodeEnum register(UserEntity userEntity, HttpServletRequest request);


    Map<Long, UserEntity> userMapList(Set<Long> userIdList);

    /**
     * 杩斿洖鐢ㄦ埛淇℃伅
     * */
    User userInfo(Long userId);

    /**
     * 鏇存柊澶村儚淇℃伅
     * */
    ReturnCodeEnum updateAvatar(User user, HttpServletRequest request);

    /**
     * 鏇存柊棣栭〉椤堕儴澶у浘
     * */
    ReturnCodeEnum updateTopImage(User user, HttpServletRequest request);

    /**
     * 鏇存柊瀵嗙爜
     * */
    ReturnCodeEnum updatePassword(PasswordDto passwordDto,
                                  HttpServletRequest request,
                                  HttpServletResponse response);

    /**
     * 鏇存柊鐢ㄦ埛鍚嶅拰绠€浠?     * */
    ReturnCodeEnum updateInfo(User user, HttpServletRequest request);


    /**
     * 娣诲姞瑙嗛涓婁紶鏁?     * */
    void addSubmitCount(Long userId,  int count);


    UserRoleEntity updateRole(UserRoleEntity userRole, HttpServletRequest request);

    /**
     * 绠＄悊鍛橀噸缃瘑鐮?     * */
    String resetPassword(UserEntity userEntity);


    ReturnCodeEnum addUser(AdminAddUserData userEntity,  HttpServletRequest request);


    void addCount(String col, Long userId, int count);


    void updateLastPublishTime(long time, long userId);

    /**
     * 寮€鍚?TOTP 涓ゆ璁よ瘉
     * @param checkOpen 妫€鏌ユ槸鍚﹀凡缁忓紑鍚簡涓ゆ璁よ瘉
     * */
    TwoFactorData openTOTP(Long userId, boolean checkOpen);


    boolean closeTOTP(TwoFactorData twoFactorData, Long userId);


    /**
     * 閲嶇疆涓ゆ璁よ瘉瀵嗛挜
     * */
    TwoFactorData recoveryTOTP(TwoFactorData twoFactorData, long userId);


    boolean checkTOTP(TwoFactorData twoFactorData, long userId);

    /**
     * 鍒涘缓鏂扮殑涓ゆ璁よ瘉瀵嗛挜
     * */
    TwoFactorData createNewTOTP(TwoFactorData twoFactorData, long userId);

    /**
     * 灏佺鐢ㄦ埛
     * */
    boolean lockUser(UserEntity userEntity, long adminUserId);


    /**
     * 蹇樿瀵嗙爜
     * */
    boolean forgotPassword(UserEntity user, String sessionId);
}

