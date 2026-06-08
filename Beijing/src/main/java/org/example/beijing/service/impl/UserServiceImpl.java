package org.example.beijing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.beijing.dto.*;
import org.example.beijing.entity.*;
import org.example.beijing.mapper.*;
import org.example.beijing.service.*;
import org.example.beijing.util.FileUploadUtil;
import org.example.beijing.util.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final JwtUtil jwtUtil;
    private final FileUploadUtil fileUploadUtil;

    // Mappers
    private final CollectionMapper collectionMapper;
    private final FootprintMapper footprintMapper;
    private final ActivitySignupMapper activitySignupMapper;
    private final RecruitApplicationMapper recruitApplicationMapper;
    private final ActivityMapper activityMapper;
    private final RecruitMapper recruitMapper;
    private final LoginLogMapper loginLogMapper;  // 新增

    // Services
    private final AIChatService aiChatService;
    private final ArtworkService artworkService;
    private final ForumService forumService;

    @Override
    public String login(LoginDTO loginDTO) {
        User user = baseMapper.selectByAccount(loginDTO.getAccount());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        String encryptedPwd = DigestUtils.md5DigestAsHex(loginDTO.getPassword().getBytes());
        if (!user.getPassword().equals(encryptedPwd)) {
            throw new RuntimeException("密码错误");
        }

        // 记录登录日志
        LoginLog log = new LoginLog();
        log.setUserId(user.getId());
        log.setLoginTime(LocalDateTime.now());
        loginLogMapper.insert(log);

        return jwtUtil.generateToken(user.getId(), user.getRole());
    }

    @Override
    public String loginByPhone(String phone) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getPhone, phone);
        User user = baseMapper.selectOne(wrapper);
        if (user == null) {
            throw new RuntimeException("该手机号未注册");
        }

        // 记录登录日志
        LoginLog log = new LoginLog();
        log.setUserId(user.getId());
        log.setLoginTime(LocalDateTime.now());
        loginLogMapper.insert(log);

        return jwtUtil.generateToken(user.getId(), user.getRole());
    }

    @Override
    @Transactional
    public synchronized void register(RegisterDTO registerDTO) {
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 检查用户名是否已存在
        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(User::getUsername, registerDTO.getUsername());
        if (baseMapper.selectCount(usernameWrapper) > 0) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查手机号是否已存在
        LambdaQueryWrapper<User> phoneWrapper = new LambdaQueryWrapper<>();
        phoneWrapper.eq(User::getPhone, registerDTO.getPhone());
        if (baseMapper.selectCount(phoneWrapper) > 0) {
            throw new RuntimeException("手机号已注册");
        }

        // 检查邮箱是否已存在（如果填写了）
        if (registerDTO.getEmail() != null && !registerDTO.getEmail().isEmpty()) {
            LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
            emailWrapper.eq(User::getEmail, registerDTO.getEmail());
            if (baseMapper.selectCount(emailWrapper) > 0) {
                throw new RuntimeException("邮箱已注册");
            }
        }

        // 计算新的ID：当前最大ID + 1
        Long maxId = baseMapper.selectMaxId();
        Long newId = (maxId == null ? 1L : maxId + 1);

        User user = new User();
        user.setId(newId);
        user.setUsername(registerDTO.getUsername());
        user.setPhone(registerDTO.getPhone());
        user.setAge(registerDTO.getAge());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(DigestUtils.md5DigestAsHex(registerDTO.getPassword().getBytes()));
        user.setInterestTags(registerDTO.getInterests());
        user.setInviteCode(registerDTO.getInviteCode());
        user.setRole("user");
        save(user);
    }

    @Override
    public void forgotPassword(String email) {
        User user = lambdaQuery().eq(User::getEmail, email).one();
        if (user == null) {
            throw new RuntimeException("该邮箱未注册");
        }
        System.out.println("已向 " + email + " 发送密码重置链接");
    }

    @Override
    public void updateUserInfo(Long userId, UpdateUserInfoDTO dto) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setUsername(dto.getUsername());
        user.setAge(dto.getAge());
        user.setBio(dto.getBio());
        user.setAvatar(dto.getAvatar());
        user.setInterestTags(dto.getInterestTags());
        updateById(user);
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        String oldEncrypted = DigestUtils.md5DigestAsHex(oldPassword.getBytes());
        if (!user.getPassword().equals(oldEncrypted)) {
            throw new RuntimeException("原密码错误");
        }
        user.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes()));
        updateById(user);
    }

    @Override
    public void resetPasswordByPhone(Long userId, String phone, String newPassword) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!user.getPhone().equals(phone)) {
            throw new RuntimeException("手机号与当前用户不匹配");
        }
        user.setPassword(DigestUtils.md5DigestAsHex(newPassword.getBytes()));
        updateById(user);
    }

    @Override
    public void applyInheritor(Long userId, String inviteCode) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!"JINGYUN-2026".equals(inviteCode)) {
            throw new RuntimeException("邀请码无效");
        }
        user.setRole("inheritor");
        updateById(user);
    }

    @Override
    public void updateAvatar(Long userId, String avatarUrl) {
        User user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setAvatar(avatarUrl);
        updateById(user);
    }

    @Override
    @Transactional
    public void deleteUserAndRelatedData(Long userId) {
        // 1. 删除头像文件
        fileUploadUtil.deleteUserAvatarFolder(userId);

        // 2. 删除收藏记录
        LambdaQueryWrapper<Collection> collectionWrapper = new LambdaQueryWrapper<>();
        collectionWrapper.eq(Collection::getUserId, userId);
        collectionMapper.delete(collectionWrapper);

        // 3. 删除足迹记录
        LambdaQueryWrapper<Footprint> footprintWrapper = new LambdaQueryWrapper<>();
        footprintWrapper.eq(Footprint::getUserId, userId);
        footprintMapper.delete(footprintWrapper);

        // 4. 删除活动报名记录
        LambdaQueryWrapper<ActivitySignup> signupWrapper = new LambdaQueryWrapper<>();
        signupWrapper.eq(ActivitySignup::getUserId, userId);
        activitySignupMapper.delete(signupWrapper);

        // 5. 删除招募申请记录
        LambdaQueryWrapper<RecruitApplication> recruitAppWrapper = new LambdaQueryWrapper<>();
        recruitAppWrapper.eq(RecruitApplication::getUserId, userId);
        recruitApplicationMapper.delete(recruitAppWrapper);

        // 6. 删除AI对话历史记录（Redis）
        aiChatService.deleteAllUserHistory(userId);

        // 7. 删除用户发布的活动（如果用户是传承人）
        LambdaQueryWrapper<Activity> activityWrapper = new LambdaQueryWrapper<>();
        activityWrapper.eq(Activity::getInheritorId, userId);
        activityMapper.delete(activityWrapper);

        // 8. 删除用户发布的招募（如果用户是传承人）
        LambdaQueryWrapper<Recruit> recruitWrapper = new LambdaQueryWrapper<>();
        recruitWrapper.eq(Recruit::getInheritorId, userId);
        recruitMapper.delete(recruitWrapper);

        // 9. 删除用户发布的作品
        artworkService.deleteByInheritorId(userId);

        // 10. 删除用户的所有评论（Redis）
        forumService.deleteUserMessages(userId);

        // 11. 最后删除用户本身
        removeById(userId);
    }
}