package org.example.beijing.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.beijing.dto.AIDTO;
import org.example.beijing.service.AIService;
import org.example.beijing.service.QwenService;
import org.example.beijing.service.WanxVideoHttpService;
import org.example.beijing.util.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AIController {

    private final AIService aiService;
    private final QwenService qwenService;
    private final WanxVideoHttpService wanxVideoHttpService;
    private final StringRedisTemplate redisTemplate;

    @PostMapping("/text")
    public ResponseResult<String> generateText(@RequestBody AIDTO.TextGenDTO dto) {
        String result = aiService.generateText(dto.getPrompt());
        return ResponseResult.success(result);
    }

    @GetMapping(value = "/text-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generateTextStream(@RequestParam String prompt) {
        SseEmitter emitter = new SseEmitter(60000L);
        qwenService.generateTextStream(prompt, emitter);
        return emitter;
    }

    @PostMapping("/image")
    public ResponseResult<String> generateImage(@RequestBody AIDTO.ImageGenDTO dto) {
        String imageUrl = aiService.generateImage(dto.getPrompt(), dto.getRefImage());
        return ResponseResult.success(imageUrl);
    }

    @PostMapping("/video")
    public ResponseResult<String> generateVideo(@RequestBody AIDTO.VideoGenDTO dto) {
        try {
            String result = wanxVideoHttpService.generateVideo(dto.getPrompt(), null, "1280*720", 5);
            return ResponseResult.success(result);
        } catch (Exception e) {
            log.error("提交视频生成任务失败", e);
            return ResponseResult.error("提交失败: " + e.getMessage());
        }
    }

    @GetMapping("/video/result")
    public ResponseResult<String> getVideoResult(@RequestParam String taskId) {
        String cacheKey = "ai:video:task:" + taskId;
        String videoUrl = redisTemplate.opsForValue().get(cacheKey);
        if (videoUrl != null) {
            if ("ERROR".equals(videoUrl)) {
                return ResponseResult.error("视频生成失败");
            }
            return ResponseResult.success(videoUrl);
        }
        return ResponseResult.success("PROCESSING");
    }
}