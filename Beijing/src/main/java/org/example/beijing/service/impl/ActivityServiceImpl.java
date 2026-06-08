package org.example.beijing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.beijing.dto.ActivityDTO;
import org.example.beijing.dto.ActivityUpdateDTO;
import org.example.beijing.entity.Activity;
import org.example.beijing.entity.ActivitySignup;
import org.example.beijing.entity.User;
import org.example.beijing.mapper.ActivityMapper;
import org.example.beijing.mapper.ActivitySignupMapper;
import org.example.beijing.service.ActivityService;
import org.example.beijing.service.UserService;
import org.example.beijing.util.FileUploadUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

    private final UserService userService;
    private final ActivitySignupMapper activitySignupMapper;
    private final FileUploadUtil fileUploadUtil;

    @Override
    public List<Activity> listByType(String type) {
        if (type == null || type.isEmpty()) {
            return list();
        }
        return baseMapper.selectByType(type);
    }

    @Override
    public List<Activity> listByTypeAndStatus(String type, Integer status) {
        autoEndExpiredActivities();
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Activity::getType, type);
        }
        if (status != null) {
            wrapper.eq(Activity::getStatus, status);
        }
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<Activity> listByInheritorId(Long inheritorId) {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Activity::getInheritorId, inheritorId);
        return baseMapper.selectList(wrapper);
    }

    private void autoEndExpiredActivities() {
        LambdaQueryWrapper<Activity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Activity::getStatus, 1)
                .lt(Activity::getEndTime, LocalDateTime.now());
        List<Activity> expiredList = baseMapper.selectList(wrapper);
        for (Activity activity : expiredList) {
            activity.setStatus(2);
            baseMapper.updateById(activity);
        }
    }

    @Override
    public Activity getDetail(Long id) {
        Activity activity = getById(id);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        return activity;
    }

    @Override
    @Transactional
    public void signup(Long activityId, Long userId) {
        Activity activity = getById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        if (activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            throw new RuntimeException("报名人数已满");
        }
        int updated = baseMapper.increaseParticipants(activityId);
        if (updated == 0) {
            throw new RuntimeException("报名失败，名额已满");
        }
    }

    @Override
    @Transactional
    public synchronized void publish(ActivityDTO.PublishDTO dto, Long userId) {
        User user = userService.getById(userId);
        if (user == null || !"inheritor".equals(user.getRole())) {
            throw new RuntimeException("只有传承人可以发布活动");
        }
        if (dto.getImageUrl() == null || dto.getImageUrl().isEmpty()) {
            throw new RuntimeException("活动封面不能为空");
        }
        Activity activity = new Activity();
        Long maxId = baseMapper.selectMaxId();
        Long newId = (maxId == null ? 1L : maxId + 1);
        activity.setId(newId);
        activity.setTitle(dto.getTitle());
        activity.setType(dto.getType());
        activity.setInheritorId(dto.getInheritorId());
        activity.setSiteId(dto.getSiteId());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setMaxParticipants(dto.getMaxParticipants());
        activity.setCurrentParticipants(0);
        activity.setDescription(dto.getDescription());
        activity.setImageUrl(dto.getImageUrl());
        activity.setStatus(1);
        save(activity);
    }

    @Override
    public void cancelSignup(Long activityId, Long userId) {
        Activity activity = getById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        activity.setCurrentParticipants(activity.getCurrentParticipants() - 1);
        updateById(activity);
    }

    @Override
    @Transactional
    public void deleteActivity(Long activityId, Long userId) {
        Activity activity = getById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        if (!activity.getInheritorId().equals(userId)) {
            throw new RuntimeException("只能删除自己发布的活动");
        }
        LambdaQueryWrapper<ActivitySignup> signupWrapper = new LambdaQueryWrapper<>();
        signupWrapper.eq(ActivitySignup::getActivityId, activityId);
        activitySignupMapper.delete(signupWrapper);

        if (activity.getImageUrl() != null) {
            fileUploadUtil.deleteFileByUrl(activity.getImageUrl());
        }

        removeById(activityId);
    }

    @Override
    @Transactional
    public void endActivity(Long activityId, Long userId) {
        Activity activity = getById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        if (!activity.getInheritorId().equals(userId)) {
            throw new RuntimeException("只能结束自己发布的活动");
        }
        activity.setStatus(2);
        updateById(activity);

        // 删除所有报名记录
        LambdaQueryWrapper<ActivitySignup> signupWrapper = new LambdaQueryWrapper<>();
        signupWrapper.eq(ActivitySignup::getActivityId, activityId);
        activitySignupMapper.delete(signupWrapper);

        // 删除活动封面图片
        if (activity.getImageUrl() != null) {
            fileUploadUtil.deleteFileByUrl(activity.getImageUrl());
        }
    }

    @Override
    @Transactional
    public void updateActivity(Long activityId, Long userId, ActivityUpdateDTO dto) {
        Activity activity = getById(activityId);
        if (activity == null) {
            throw new RuntimeException("活动不存在");
        }
        if (!activity.getInheritorId().equals(userId)) {
            throw new RuntimeException("只能编辑自己发布的活动");
        }

        if (dto.getImageUrl() != null && !dto.getImageUrl().equals(activity.getImageUrl())) {
            if (activity.getImageUrl() != null) {
                fileUploadUtil.deleteFileByUrl(activity.getImageUrl());
            }
        }

        activity.setTitle(dto.getTitle());
        activity.setType(dto.getType());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setMaxParticipants(dto.getMaxParticipants());
        activity.setDescription(dto.getDescription());
        activity.setImageUrl(dto.getImageUrl());
        activity.setSiteId(dto.getSiteId());

        updateById(activity);
    }
}