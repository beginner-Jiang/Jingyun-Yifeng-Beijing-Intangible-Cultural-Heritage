package org.example.beijing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.beijing.service.*;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {

    private final QwenService qwenService;
    private final WanxImageService wanxImageService;
    private final WanxVideoHttpService wanxVideoHttpService; // 新增

    @Override
    public String generateText(String prompt) {
        return qwenService.generateText(prompt);
    }

    @Override
    public String generateImage(String prompt, String refImage) {
        return wanxImageService.generateImage(prompt, refImage);
    }

    @Override
    public String generateVideo(String prompt, String refVideo) {
        try {
            // 默认分辨率 1280*720，时长5秒，无参考音频
            return wanxVideoHttpService.generateVideo(prompt, null, "1280*720", 5);
        } catch (Exception e) {
            log.error("视频生成失败", e);
            throw new RuntimeException("视频生成失败: " + e.getMessage());
        }
    }
}