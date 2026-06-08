package org.example.beijing.controller;

import lombok.RequiredArgsConstructor;
import org.example.beijing.entity.ForumMessage;
import org.example.beijing.service.ForumService;
import org.example.beijing.util.ResponseResult;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/forum")
@RequiredArgsConstructor
public class ForumController {

    private final ForumService forumService;

    @GetMapping("/messages")
    public ResponseResult<List<ForumMessage>> getMessages(@RequestParam(defaultValue = "30") int limit) {
        List<ForumMessage> messages = forumService.getMessages(limit);
        return ResponseResult.success(messages);
    }

    @PostMapping("/post")
    public ResponseResult<?> postMessage(@RequestBody PostMessageRequest request, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        // 如果需要用户名，可从 request 中获取，或在 service 中查询
        forumService.postMessage(userId, null, request.getContent());
        return ResponseResult.success("发布成功");
    }

    public static class PostMessageRequest {
        private String content;
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}