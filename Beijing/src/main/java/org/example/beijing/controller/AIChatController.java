package org.example.beijing.controller;

import lombok.RequiredArgsConstructor;
import org.example.beijing.entity.AIChatMessage;
import org.example.beijing.service.AIChatService;
import org.example.beijing.util.ResponseResult;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/ai/chat")
@RequiredArgsConstructor
public class AIChatController {

    private final AIChatService aiChatService;

    @GetMapping("/history")
    public ResponseResult<List<AIChatMessage>> getHistory(@RequestParam String mode, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseResult.error(401, "未登录");
        }
        List<AIChatMessage> history = aiChatService.getHistory(userId, mode, 10);
        return ResponseResult.success(history);
    }

    @PostMapping("/save")
    public ResponseResult<?> saveMessage(@RequestBody SaveRequest requestBody, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseResult.error(401, "未登录");
        }
        aiChatService.saveMessage(userId, requestBody.getMode(), requestBody.getMessage());
        return ResponseResult.success();
    }

    @DeleteMapping("/clear")
    public ResponseResult<?> clearHistory(@RequestParam String mode, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            return ResponseResult.error(401, "未登录");
        }
        aiChatService.clearHistory(userId, mode);
        return ResponseResult.success("清空成功");
    }

    public static class SaveRequest {
        private String mode;
        private AIChatMessage message;
        public String getMode() { return mode; }
        public void setMode(String mode) { this.mode = mode; }
        public AIChatMessage getMessage() { return message; }
        public void setMessage(AIChatMessage message) { this.message = message; }
    }
}