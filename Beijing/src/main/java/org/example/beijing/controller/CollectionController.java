package org.example.beijing.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.beijing.service.CollectionService;
import org.example.beijing.util.ResponseResult;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/collection")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @PostMapping("/add")
    public ResponseResult<?> add(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseResult.error(401, "用户未登录");
            }
            String targetType = (String) params.get("targetType");
            if (targetType == null) {
                return ResponseResult.badRequest("缺少 targetType 参数");
            }
            Object targetIdObj = params.get("targetId");
            if (targetIdObj == null) {
                return ResponseResult.badRequest("缺少 targetId 参数");
            }
            Long targetId = Long.parseLong(targetIdObj.toString());
            collectionService.addCollection(userId, targetType, targetId);
            return ResponseResult.success("收藏成功");
        } catch (NumberFormatException e) {
            log.error("targetId 格式错误", e);
            return ResponseResult.badRequest("targetId 格式错误");
        } catch (Exception e) {
            log.error("收藏失败", e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @DeleteMapping("/remove")
    public ResponseResult<?> remove(@RequestParam String targetType, @RequestParam Long targetId, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseResult.error(401, "用户未登录");
            }
            collectionService.removeCollection(userId, targetType, targetId);
            return ResponseResult.success("取消收藏成功");
        } catch (Exception e) {
            log.error("取消收藏失败", e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @GetMapping("/list")
    public ResponseResult<?> list(@RequestParam String targetType, @RequestParam(defaultValue = "16") int limit, HttpServletRequest request) {
        try {
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseResult.error(401, "用户未登录");
            }
            switch (targetType) {
                case "item":
                    return ResponseResult.success(collectionService.getItemCollections(userId, limit));
                case "site":
                    return ResponseResult.success(collectionService.getSiteCollections(userId, limit));
                case "activity":
                    return ResponseResult.success(collectionService.getActivityCollections(userId, limit));
                default:
                    return ResponseResult.badRequest("未知的 targetType");
            }
        } catch (Exception e) {
            log.error("获取收藏列表失败", e);
            return ResponseResult.error(e.getMessage());
        }
    }

    @GetMapping("/check")
    public ResponseResult<?> check(@RequestParam(required = false) String targetType,
                                   @RequestParam(required = false) Long targetId,
                                   HttpServletRequest request) {
        try {
            if (targetType == null || targetId == null) {
                return ResponseResult.badRequest("缺少必要参数 targetType 或 targetId");
            }
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return ResponseResult.error(401, "用户未登录");
            }
            boolean collected = collectionService.isCollected(userId, targetType, targetId);
            Map<String, Boolean> result = new HashMap<>();
            result.put("collected", collected);
            return ResponseResult.success(result);
        } catch (Exception e) {
            log.error("检查收藏状态失败", e);
            return ResponseResult.error(e.getMessage());
        }
    }
}