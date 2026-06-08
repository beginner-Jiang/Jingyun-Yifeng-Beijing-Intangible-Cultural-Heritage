package org.example.beijing.service;

import org.example.beijing.dto.AIDTO;

/**
 * AI 服务接口
 */
public interface AIService {

    /**
     * 文生文：非遗问答
     * @param prompt 用户输入的问题
     * @return AI 生成的回答
     */
    String generateText(String prompt);

    /**
     * 文生图：生成非遗风格海报
     * @param prompt 图片描述
     * @param refImage 参考图片URL（可选）
     * @return 生成的图片URL
     */
    String generateImage(String prompt, String refImage);

    /**
     * 文生视频：生成非遗介绍视频
     * @param prompt 视频描述
     * @param refVideo 参考视频URL（可选）
     * @return 生成的视频URL
     */
    String generateVideo(String prompt, String refVideo);
}