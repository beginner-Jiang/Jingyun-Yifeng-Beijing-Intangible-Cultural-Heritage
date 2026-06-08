package org.example.beijing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.beijing.dto.*;
import org.example.beijing.entity.User;

public interface UserService extends IService<User> {

    String login(LoginDTO loginDTO);

    String loginByPhone(String phone);

    void register(RegisterDTO registerDTO);

    void forgotPassword(String email);

    void updateUserInfo(Long userId, UpdateUserInfoDTO dto);

    void changePassword(Long userId, String oldPassword, String newPassword);

    void resetPasswordByPhone(Long userId, String phone, String newPassword);

    void applyInheritor(Long userId, String inviteCode);

    void updateAvatar(Long userId, String avatarUrl);

    void deleteUserAndRelatedData(Long userId);   // 新增：注销用户并删除所有相关数据
}