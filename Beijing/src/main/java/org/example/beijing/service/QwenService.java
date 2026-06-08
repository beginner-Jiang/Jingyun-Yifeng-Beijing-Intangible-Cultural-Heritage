package org.example.beijing.service;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.ResultCallback;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.InputRequiredException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.beijing.config.DashScopeConfig;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class QwenService {

    private final DashScopeConfig dashScopeConfig;
    private final StringRedisTemplate redisTemplate;

    private static final String CACHE_PREFIX = "ai:text:";
    private static final long CACHE_TTL_HOURS = 1;

    /**
     * 同步生成文本（保留原接口，用于非流式场景）
     */
    public String generateText(String prompt) {
        try {
            String cacheKey = CACHE_PREFIX + DigestUtils.md5DigestAsHex(prompt.getBytes());
            String cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                log.info("命中缓存，prompt: {}", prompt);
                return cached;
            }

            Generation gen = new Generation();
            GenerationParam param = GenerationParam.builder()
                    .model("qwen-max")
                    .prompt(prompt)
                    .apiKey(dashScopeConfig.getApiKey())
                    .build();
            GenerationResult result = gen.call(param);
            String text = result.getOutput().getText();

            redisTemplate.opsForValue().set(cacheKey, text, CACHE_TTL_HOURS, TimeUnit.HOURS);
            return text;
        } catch (NoApiKeyException | InputRequiredException e) {
            log.error("调用通义千问失败", e);
            throw new RuntimeException("AI 服务调用失败: " + e.getMessage());
        }
    }

    /**
     * 流式生成文本（SSE）
     * 使用 SDK 的 ResultCallback 接口
     */
    public void generateTextStream(String prompt, SseEmitter emitter) {
        String cacheKey = CACHE_PREFIX + DigestUtils.md5DigestAsHex(prompt.getBytes());
        try {
            String cached = redisTemplate.opsForValue().get(cacheKey);
            if (cached != null) {
                emitter.send(cached);
                emitter.complete();
                return;
            }

            Generation gen = new Generation();
            GenerationParam param = GenerationParam.builder()
                    .model("qwen-max")
                    .prompt(prompt)
                    .apiKey(dashScopeConfig.getApiKey())
                    .incrementalOutput(true)
                    .build();

            StringBuilder fullText = new StringBuilder();

            gen.streamCall(param, new ResultCallback<GenerationResult>() {
                @Override
                public void onEvent(GenerationResult result) {
                    try {
                        String delta = result.getOutput().getText();
                        fullText.append(delta);
                        emitter.send(delta);
                    } catch (IOException e) {
                        log.error("SSE发送失败", e);
                        emitter.completeWithError(e);
                    }
                }

                @Override
                public void onComplete() {
                    redisTemplate.opsForValue().set(cacheKey, fullText.toString(), CACHE_TTL_HOURS, TimeUnit.HOURS);
                    emitter.complete();
                }

                @Override
                public void onError(Exception e) {
                    log.error("流式生成失败", e);
                    emitter.completeWithError(e);
                }
            });
        } catch (Exception e) {
            log.error("启动流式生成失败", e);
            emitter.completeWithError(e);
        }
    }
}