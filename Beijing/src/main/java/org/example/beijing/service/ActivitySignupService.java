package org.example.beijing.service;

import org.example.beijing.entity.Activity;
import org.example.beijing.entity.ActivitySignup;

import java.util.List;

public interface ActivitySignupService {
    void signup(Long activityId, Long userId);
    void cancelSignup(Long activityId, Long userId);
    List<Activity> getUserSignups(Long userId);
    List<ActivitySignup> getActivitySignups(Long activityId, Long requesterId);
    void approveSignup(Long signupId, Long approverId);
    void rejectSignup(Long signupId, Long approverId);
    boolean hasConflict(Long activityId, Long userId, Long excludeSignupId);
    int getUserSignupCount(Long userId);
    void deleteByActivityId(Long activityId);
}