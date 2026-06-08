package org.example.beijing.controller;

import lombok.RequiredArgsConstructor;
import org.example.beijing.dto.ActivityDTO;
import org.example.beijing.dto.ActivitySignupDTO;
import org.example.beijing.dto.ActivityUpdateDTO;
import org.example.beijing.entity.Activity;
import org.example.beijing.entity.ActivitySignup;
import org.example.beijing.entity.User;
import org.example.beijing.mapper.UserMapper;
import org.example.beijing.service.ActivityService;
import org.example.beijing.service.ActivitySignupService;
import org.example.beijing.util.ResponseResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final ActivitySignupService signupService;
    private final UserMapper userMapper;

    @GetMapping("/list")
    public ResponseResult<List<Activity>> list(@RequestParam(required = false) String type,
                                               @RequestParam(required = false) Integer status,
                                               @RequestParam(required = false) Long inheritorId) {
        List<Activity> list;
        if (inheritorId != null) {
            list = activityService.listByInheritorId(inheritorId);
        } else {
            list = activityService.listByTypeAndStatus(type, status);
        }
        return ResponseResult.success(list);
    }

    @GetMapping("/{id}")
    public ResponseResult<Activity> detail(@PathVariable Long id) {
        Activity activity = activityService.getDetail(id);
        return ResponseResult.success(activity);
    }

    @PostMapping("/signup/{id}")
    public ResponseResult<?> signup(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        signupService.signup(id, userId);
        return ResponseResult.success("申请已提交，等待审核");
    }

    @PostMapping("/publish")
    public ResponseResult<?> publish(@RequestBody ActivityDTO.PublishDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        activityService.publish(dto, userId);
        return ResponseResult.success("活动发布成功");
    }

    @DeleteMapping("/cancel/{id}")
    public ResponseResult<?> cancelSignup(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        signupService.cancelSignup(id, userId);
        return ResponseResult.success("已取消报名");
    }

    @GetMapping("/my-signups")
    public ResponseResult<List<Activity>> mySignups(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Activity> list = signupService.getUserSignups(userId);
        return ResponseResult.success(list);
    }

    @DeleteMapping("/{id}")
    public ResponseResult<?> deleteActivity(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        activityService.deleteActivity(id, userId);
        return ResponseResult.success("删除成功");
    }

    @PostMapping("/end/{id}")
    public ResponseResult<?> endActivity(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        activityService.endActivity(id, userId);
        return ResponseResult.success("活动已结束");
    }

    @PutMapping("/{id}")
    public ResponseResult<?> updateActivity(@PathVariable Long id,
                                            @RequestBody ActivityUpdateDTO dto,
                                            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        activityService.updateActivity(id, userId, dto);
        return ResponseResult.success("更新成功");
    }

    @GetMapping("/{id}/signups")
    public ResponseResult<List<ActivitySignupDTO>> getSignups(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<ActivitySignup> signups = signupService.getActivitySignups(id, userId);
        List<ActivitySignupDTO> dtos = signups.stream().map(s -> {
            ActivitySignupDTO dto = new ActivitySignupDTO();
            dto.setId(s.getId());
            dto.setActivityId(s.getActivityId());
            dto.setUserId(s.getUserId());
            User user = userMapper.selectById(s.getUserId());
            dto.setUsername(user != null ? user.getUsername() : "未知用户");
            dto.setStatus(s.getStatus());
            dto.setApplyTime(s.getApplyTime());
            dto.setProcessTime(s.getProcessTime());
            return dto;
        }).collect(Collectors.toList());
        return ResponseResult.success(dtos);
    }

    @PostMapping("/signup/{signupId}/approve")
    public ResponseResult<?> approveSignup(@PathVariable Long signupId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        signupService.approveSignup(signupId, userId);
        return ResponseResult.success("已同意");
    }

    @PostMapping("/signup/{signupId}/reject")
    public ResponseResult<?> rejectSignup(@PathVariable Long signupId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        signupService.rejectSignup(signupId, userId);
        return ResponseResult.success("已拒绝");
    }
}