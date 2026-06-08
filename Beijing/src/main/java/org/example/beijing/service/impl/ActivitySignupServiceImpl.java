package org.example.beijing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.beijing.entity.Activity;
import org.example.beijing.entity.ActivitySignup;
import org.example.beijing.entity.User;
import org.example.beijing.mapper.ActivityMapper;
import org.example.beijing.mapper.ActivitySignupMapper;
import org.example.beijing.mapper.UserMapper;
import org.example.beijing.service.ActivityService;
import org.example.beijing.service.ActivitySignupService;
import org.example.beijing.service.CacheService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivitySignupServiceImpl implements ActivitySignupService {

    private final ActivitySignupMapper signupMapper;
    private final ActivityService activityService;
    private final ActivityMapper activityMapper;
    private final UserMapper userMapper;
    private final CacheService cacheService;

    @Override
    @Transactional
    public void signup(Long activityId, Long userId) {
        Activity activity = activityService.getById(activityId);
        if (activity == null || activity.getStatus() != 1) {
            throw new RuntimeException("活动不存在或已结束");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(activity.getEndTime())) {
            throw new RuntimeException("活动已结束");
        }

        ActivitySignup existing = signupMapper.selectByActivityAndUser(activityId, userId);
        if (existing != null) {
            throw new RuntimeException("已经申请过此活动");
        }

        ActivitySignup signup = new ActivitySignup();
        signup.setActivityId(activityId);
        signup.setUserId(userId);
        signup.setStatus("pending");
        signup.setApplyTime(LocalDateTime.now());
        signupMapper.insert(signup);

        cacheService.delete(cacheService.buildUserActivitiesKey(userId));
    }

    @Override
    @Transactional
    public void cancelSignup(Long activityId, Long userId) {
        LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivitySignup::getActivityId, activityId)
                .eq(ActivitySignup::getUserId, userId);
        int deleted = signupMapper.delete(wrapper);
        if (deleted == 0) {
            throw new RuntimeException("未找到报名记录");
        }
        cacheService.delete(cacheService.buildUserActivitiesKey(userId));
    }

    @Override
    public List<Activity> getUserSignups(Long userId) {
        String cacheKey = cacheService.buildUserActivitiesKey(userId);
        List<Activity> cached = cacheService.getList(cacheKey, Activity.class);
        if (cached != null) {
            return cached;
        }
        List<Activity> list = signupMapper.selectActivitiesByUserId(userId);
        cacheService.setList(cacheKey, list);
        return list;
    }

    @Override
    public List<ActivitySignup> getActivitySignups(Long activityId, Long requesterId) {
        // 修改：移除权限检查，任何登录用户都可以查看报名列表
        // 原逻辑：Activity activity = activityService.getById(activityId);
        // if (activity == null) throw new RuntimeException("活动不存在");
        // if (!activity.getInheritorId().equals(requesterId)) throw new RuntimeException("无权查看此活动的报名");
        // 现在直接返回报名列表，不限制查看者
        return signupMapper.selectByActivityId(activityId);
    }

    @Override
    @Transactional
    public void approveSignup(Long signupId, Long approverId) {
        ActivitySignup signup = signupMapper.selectById(signupId);
        if (signup == null) {
            throw new RuntimeException("报名记录不存在");
        }
        Activity activity = activityService.getById(signup.getActivityId());
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        if (!activity.getInheritorId().equals(approverId)) {
            throw new RuntimeException("无权操作");
        }
        if (!"pending".equals(signup.getStatus())) {
            throw new RuntimeException("该申请已被处理");
        }
        if (activity.getStatus() != 1) {
            throw new RuntimeException("活动已结束");
        }
        if (activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            throw new RuntimeException("活动人数已满");
        }

        int acceptedCount = signupMapper.selectList(new LambdaQueryWrapper<ActivitySignup>()
                .eq(ActivitySignup::getUserId, signup.getUserId())
                .eq(ActivitySignup::getStatus, "accepted")).size();
        if (acceptedCount >= 3) {
            throw new RuntimeException("该用户已参与3个活动，无法再接受");
        }

        if (hasConflict(activity.getId(), signup.getUserId(), signup.getId())) {
            throw new RuntimeException("该用户有其他活动时间冲突");
        }

        signup.setStatus("accepted");
        signup.setProcessTime(LocalDateTime.now());
        signupMapper.updateById(signup);

        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
        activityService.updateById(activity);

        cacheService.delete(cacheService.buildUserActivitiesKey(signup.getUserId()));
    }

    @Override
    @Transactional
    public void rejectSignup(Long signupId, Long approverId) {
        ActivitySignup signup = signupMapper.selectById(signupId);
        if (signup == null) {
            throw new RuntimeException("报名记录不存在");
        }
        Activity activity = activityService.getById(signup.getActivityId());
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        if (!activity.getInheritorId().equals(approverId)) {
            throw new RuntimeException("无权操作");
        }
        if (!"pending".equals(signup.getStatus())) {
            throw new RuntimeException("该申请已被处理");
        }

        signup.setStatus("rejected");
        signup.setProcessTime(LocalDateTime.now());
        signupMapper.updateById(signup);

        cacheService.delete(cacheService.buildUserActivitiesKey(signup.getUserId()));
    }

    @Override
    public boolean hasConflict(Long activityId, Long userId, Long excludeSignupId) {
        List<ActivitySignup> userSignups = signupMapper.selectList(new LambdaQueryWrapper<ActivitySignup>()
                .eq(ActivitySignup::getUserId, userId)
                .eq(ActivitySignup::getStatus, "accepted"));
        Activity targetActivity = activityService.getById(activityId);
        if (targetActivity == null) return false;

        LocalDateTime targetStart = targetActivity.getStartTime();
        LocalDateTime targetEnd = targetActivity.getEndTime();

        for (ActivitySignup as : userSignups) {
            if (as.getId().equals(excludeSignupId)) continue;
            Activity act = activityService.getById(as.getActivityId());
            if (act == null) continue;
            if (targetStart.isBefore(act.getEndTime()) && targetEnd.isAfter(act.getStartTime())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getUserSignupCount(Long userId) {
        return signupMapper.countByUserId(userId);
    }

    @Override
    @Transactional
    public void deleteByActivityId(Long activityId) {
        LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivitySignup::getActivityId, activityId);
        List<ActivitySignup> signups = signupMapper.selectList(wrapper);
        for (ActivitySignup signup : signups) {
            cacheService.delete(cacheService.buildUserActivitiesKey(signup.getUserId()));
        }
        signupMapper.delete(wrapper);
    }
}