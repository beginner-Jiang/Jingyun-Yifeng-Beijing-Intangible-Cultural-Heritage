package org.example.beijing.controller;

import lombok.RequiredArgsConstructor;
import org.example.beijing.dto.RecruitDTO;
import org.example.beijing.dto.RecruitUpdateDTO;
import org.example.beijing.entity.RecruitApplication;
import org.example.beijing.entity.User;
import org.example.beijing.mapper.UserMapper;
import org.example.beijing.service.RecruitService;
import org.example.beijing.util.ResponseResult;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recruit")
@RequiredArgsConstructor
public class RecruitController {

    private final RecruitService recruitService;
    private final UserMapper userMapper;

    @PostMapping("/add")
    public ResponseResult<?> addPost(@RequestBody RecruitDTO.AddPost dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        recruitService.addPost(userId, dto);
        return ResponseResult.success("发布成功");
    }

    @GetMapping("/list")
    public ResponseResult<?> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseResult.success(recruitService.getRecruitList(userId));
    }

    @PostMapping("/apply")
    public ResponseResult<?> apply(@RequestBody RecruitDTO.Apply dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        recruitService.applyRecruit(dto.getRecruitId(), userId);
        return ResponseResult.success("申请已提交");
    }

    @PostMapping("/end")
    public ResponseResult<?> end(@RequestBody RecruitDTO.End dto, HttpServletRequest request) {
        Long inheritorId = (Long) request.getAttribute("userId");
        recruitService.endRecruit(dto.getRecruitId(), inheritorId);
        return ResponseResult.success("招募已结束");
    }

    @PutMapping("/{id}")
    public ResponseResult<?> updateRecruit(@PathVariable Long id,
                                           @RequestBody RecruitUpdateDTO dto,
                                           HttpServletRequest request) {
        Long inheritorId = (Long) request.getAttribute("userId");
        recruitService.updateRecruit(id, inheritorId, dto);
        return ResponseResult.success("更新成功");
    }

    @GetMapping("/{id}/applications")
    public ResponseResult<List<Map<String, Object>>> getApplications(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<RecruitApplication> apps = recruitService.getRecruitApplications(id, userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (RecruitApplication app : apps) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", app.getId());
            map.put("recruitId", app.getRecruitId());
            map.put("userId", app.getUserId());
            User user = userMapper.selectById(app.getUserId());
            map.put("username", user != null ? user.getUsername() : "未知");
            map.put("status", app.getStatus());
            map.put("applyTime", app.getApplyTime());
            map.put("processTime", app.getProcessTime());
            result.add(map);
        }
        return ResponseResult.success(result);
    }

    @PostMapping("/application/{appId}/approve")
    public ResponseResult<?> approveApplication(@PathVariable Long appId, HttpServletRequest request) {
        Long inheritorId = (Long) request.getAttribute("userId");
        recruitService.approveApplication(appId, inheritorId);
        return ResponseResult.success("已同意");
    }

    @PostMapping("/application/{appId}/reject")
    public ResponseResult<?> rejectApplication(@PathVariable Long appId, HttpServletRequest request) {
        Long inheritorId = (Long) request.getAttribute("userId");
        recruitService.rejectApplication(appId, inheritorId);
        return ResponseResult.success("已拒绝");
    }

    @GetMapping("/my-applications")
    public ResponseResult<?> myApplications(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseResult.success(recruitService.getUserApplications(userId));
    }

    @GetMapping("/my-posts")
    public ResponseResult<?> myPosts(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return ResponseResult.success(recruitService.getUserPosts(userId));
    }
}