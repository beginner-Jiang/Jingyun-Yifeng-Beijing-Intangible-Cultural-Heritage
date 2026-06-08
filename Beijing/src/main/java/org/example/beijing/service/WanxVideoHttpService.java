package org.example.beijing.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.example.beijing.config.DashScopeConfig;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class WanxVideoHttpService {

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    private final DashScopeConfig dashScopeConfig;

    public WanxVideoHttpService(DashScopeConfig dashScopeConfig) {
        this.dashScopeConfig = dashScopeConfig;
    }

    private static final String BASE_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/video-generation/video-synthesis";
    private static final String TASK_URL_PREFIX = "https://dashscope.aliyuncs.com/api/v1/tasks/";

    public String submitTask(String prompt, String audioUrl, String size, int duration) throws IOException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "wan2.6-t2v");

        JSONObject input = new JSONObject();
        input.put("prompt", prompt);
        if (audioUrl != null && !audioUrl.isEmpty()) {
            input.put("audio_url", audioUrl);
        }
        requestBody.put("input", input);

        JSONObject parameters = new JSONObject();
        parameters.put("size", size);
        parameters.put("duration", duration);
        parameters.put("prompt_extend", true);
        requestBody.put("parameters", parameters);

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(RequestBody.create(MediaType.parse("application/json"), requestBody.toString()))
                .addHeader("Authorization", "Bearer " + dashScopeConfig.getApiKey())
                .addHeader("X-DashScope-Async", "enable")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String errorBody = response.body() != null ? response.body().string() : "";
                throw new IOException("提交任务失败，HTTP " + response.code() + ": " + errorBody);
            }
            String respBody = response.body().string();
            log.debug("提交任务响应: {}", respBody);
            JSONObject resp = JSON.parseObject(respBody);
            String taskId = resp.getJSONObject("output").getString("task_id");
            if (taskId == null || taskId.isEmpty()) {
                throw new IOException("提交任务响应中缺少task_id");
            }
            return taskId;
        }
    }

    /**
     * 轮询任务结果，最多等待60秒（12次*5秒）
     * 特别处理 processing: 占位符，继续等待
     */
    public String fetchResult(String taskId) throws IOException, InterruptedException {
        String url = TASK_URL_PREFIX + taskId;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Authorization", "Bearer " + dashScopeConfig.getApiKey())
                .build();

        int maxAttempts = 12;  // 总共60秒
        int processingWaitCount = 0;
        for (int i = 0; i < maxAttempts; i++) {
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    log.warn("查询任务失败，HTTP {}，第{}次重试", response.code(), i + 1);
                    Thread.sleep(5000);
                    continue;
                }
                String respBody = response.body().string();
                log.info("查询任务响应 (taskId={}, attempt={})", taskId, i + 1);
                log.debug("完整响应: {}", respBody);
                JSONObject resp = JSON.parseObject(respBody);
                JSONObject output = resp.getJSONObject("output");
                if (output == null) {
                    throw new IOException("响应中缺少output字段");
                }
                String taskStatus = output.getString("task_status");
                if ("SUCCEEDED".equals(taskStatus)) {
                    JSONObject result = output.getJSONObject("result");
                    String videoUrl = null;
                    if (result != null) {
                        videoUrl = result.getString("video_url");
                    }
                    if (videoUrl == null) {
                        videoUrl = output.getString("video_url");
                    }
                    // 处理阿里云可能返回的占位符（processing:xxx）
                    if (videoUrl == null || videoUrl.isEmpty() || videoUrl.startsWith("processing:")) {
                        log.warn("视频URL无效或仍为占位符: {}, 可能是任务尚未完全准备好，继续等待", videoUrl);
                        processingWaitCount++;
                        if (processingWaitCount <= 6) { // 最多额外等待30秒
                            Thread.sleep(5000);
                            continue;
                        } else {
                            throw new IOException("视频生成超时：长时间返回占位符");
                        }
                    }
                    log.info("视频生成成功，URL: {}", videoUrl);
                    return videoUrl;
                } else if ("FAILED".equals(taskStatus)) {
                    String message = output.getString("message");
                    throw new IOException("视频生成失败: " + (message != null ? message : "未知错误"));
                }
                // PENDING 或 RUNNING 继续等待
            }
            Thread.sleep(5000);
        }
        throw new IOException("视频生成超时（60秒后仍未完成）");
    }

    public String generateVideo(String prompt, String audioUrl, String size, int duration) throws IOException, InterruptedException {
        int maxRetries = 2;
        Exception lastException = null;
        for (int retry = 0; retry <= maxRetries; retry++) {
            try {
                String taskId = submitTask(prompt, audioUrl, size, duration);
                log.info("视频任务已提交，taskId: {}", taskId);
                String videoUrl = fetchResult(taskId);
                log.info("视频生成成功，URL: {}", videoUrl);
                return videoUrl;
            } catch (Exception e) {
                lastException = e;
                log.error("视频生成失败，重试次数: {}/{}，错误: {}", retry, maxRetries, e.getMessage());
                if (retry == maxRetries) {
                    throw new RuntimeException("视频生成失败: " + e.getMessage());
                }
                Thread.sleep(3000);
            }
        }
        throw new RuntimeException("视频生成失败: " + (lastException != null ? lastException.getMessage() : "未知错误"));
    }
}