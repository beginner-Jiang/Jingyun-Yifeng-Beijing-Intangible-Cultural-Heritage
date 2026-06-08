package org.example.beijing.service;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.beijing.config.DashScopeConfig;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WanxImageService {

    private final DashScopeConfig dashScopeConfig;

    public String generateImage(String prompt, String refImage) {
        try {
            ImageSynthesis is = new ImageSynthesis();
            ImageSynthesisParam param = ImageSynthesisParam.builder()
                    .model("wanx-v1")
                    .prompt(prompt)
                    .n(1)
                    .size("1024*1024")
                    .apiKey(dashScopeConfig.getApiKey())
                    .build();
            ImageSynthesisResult result = is.call(param);

            // 将结果转为 JSON 对象，动态提取 URL
            String json = JSON.toJSONString(result);
            JSONObject obj = JSON.parseObject(json);
            // 尝试获取 url 或 image_url 字段
            String url = obj.getJSONObject("output")
                    .getJSONArray("results")
                    .getJSONObject(0)
                    .getString("url");
            if (url == null) {
                url = obj.getJSONObject("output")
                        .getJSONArray("results")
                        .getJSONObject(0)
                        .getString("image_url");
            }
            return url;
        } catch (NoApiKeyException e) {
            log.error("调用通义万相文生图失败", e);
            throw new RuntimeException("AI 服务调用失败: " + e.getMessage());
        }
    }
}