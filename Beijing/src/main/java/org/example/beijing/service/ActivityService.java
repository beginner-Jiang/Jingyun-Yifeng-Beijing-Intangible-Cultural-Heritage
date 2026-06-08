package org.example.beijing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.beijing.dto.ActivityDTO;
import org.example.beijing.dto.ActivityUpdateDTO;
import org.example.beijing.entity.Activity;
import java.util.List;

public interface ActivityService extends IService<Activity> {
    List<Activity> listByType(String type);
    List<Activity> listByTypeAndStatus(String type, Integer status);
    List<Activity> listByInheritorId(Long inheritorId);
    Activity getDetail(Long id);
    void signup(Long activityId, Long userId);
    void publish(ActivityDTO.PublishDTO dto, Long userId);
    void cancelSignup(Long activityId, Long userId);
    void deleteActivity(Long activityId, Long userId);
    void endActivity(Long activityId, Long userId);
    void updateActivity(Long activityId, Long userId, ActivityUpdateDTO dto);
}