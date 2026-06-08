package org.example.beijing.controller;

import lombok.RequiredArgsConstructor;
import org.example.beijing.dto.ChangePasswordByPhoneDTO;
import org.example.beijing.dto.UpdateUserInfoDTO;
import org.example.beijing.entity.User;
import org.example.beijing.service.UserService;
import org.example.beijing.util.FileUploadUtil;
import org.example.beijing.util.JwtUtil;
import org.example.beijing.util.ResponseResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final FileUploadUtil fileUploadUtil;

    @GetMapping("/info")
    public ResponseResult<User> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getById(userId);
        user.setPassword(null);
        return ResponseResult.success(user);
    }

    @PutMapping("/update")
    public ResponseResult<?> updateUserInfo(@RequestBody UpdateUserInfoDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updateUserInfo(userId, dto);
        return ResponseResult.success("更新成功");
    }

    @PostMapping("/reset-password-by-phone")
    public ResponseResult<?> resetPasswordByPhone(@RequestBody @Valid ChangePasswordByPhoneDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userService.resetPasswordByPhone(userId, dto.getPhone(), dto.getNewPassword());
        return ResponseResult.success("密码修改成功");
    }

    @PostMapping("/apply-inheritor")
    public ResponseResult<?> applyInheritor(@RequestParam String inviteCode, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userService.applyInheritor(userId, inviteCode);
        return ResponseResult.success("申请已提交，等待审核");
    }

    @PostMapping("/upload-avatar")
    public ResponseResult<String> uploadAvatar(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (file.isEmpty()) {
            return ResponseResult.badRequest("文件不能为空");
        }
        String avatarUrl = fileUploadUtil.saveAvatar(file, userId);
        userService.updateAvatar(userId, avatarUrl);
        return ResponseResult.success(avatarUrl);
    }

    @DeleteMapping("/delete")
    public ResponseResult<?> deleteUser(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userService.deleteUserAndRelatedData(userId);
        return ResponseResult.success("账户已注销");
    }

    @GetMapping("/radar")
    public ResponseResult<Map<String, Object>> getUserRadar(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        User user = userService.getById(userId);
        List<Integer> data = calculateRadarData(user);
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        return ResponseResult.success(result);
    }

    private List<Integer> calculateRadarData(User user) {
        String interestTags = user.getInterestTags();
        int[] scores = new int[6];

        if (interestTags != null && !interestTags.isEmpty()) {
            String[] tags = interestTags.split(",");
            for (String tag : tags) {
                tag = tag.trim();
                switch (tag) {
                    case "传统技艺":
                        scores[0] += 30;
                        break;
                    case "曲艺":
                        scores[1] += 30;
                        break;
                    case "传统戏剧":
                        scores[2] += 30;
                        break;
                    case "民俗":
                        scores[3] += 30;
                        break;
                    case "传统医药":
                        scores[4] += 30;
                        break;
                    case "民间文学":
                        scores[5] += 30;
                        break;
                    default:
                        for (int i = 0; i < scores.length; i++) {
                            scores[i] += 5;
                        }
                        break;
                }
            }
        }

        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > 100) {
                scores[i] = 100;
            }
        }

        return Arrays.asList(scores[0], scores[1], scores[2], scores[3], scores[4], scores[5]);
    }
}