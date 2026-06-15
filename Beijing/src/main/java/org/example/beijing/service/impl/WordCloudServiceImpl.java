package org.example.beijing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.beijing.entity.AIChatMessage;
import org.example.beijing.entity.ForumMessage;
import org.example.beijing.mapper.CommentMapper;
import org.example.beijing.service.ForumService;
import org.example.beijing.service.WordCloudService;
import org.example.beijing.util.TextFilterUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordCloudServiceImpl implements WordCloudService {

    private final ForumService forumService;
    private final CommentMapper commentMapper;
    private final TextFilterUtil textFilterUtil;
    private final StringRedisTemplate redisTemplate;

    private static final String AI_CHAT_KEY_PREFIX = "ai:chat:";

    @Override
    public Map<String, Integer> getWordCloud(LocalDateTime start, LocalDateTime end, int limit) {
        List<String> allTexts = new ArrayList<>();

        // 1. 从 Redis 获取 AI 对话消息（只取用户的 text 消息，排除 AI 助手的回复和欢迎语）
        try {
            Set<String> keys = redisTemplate.keys(AI_CHAT_KEY_PREFIX + "*:text");
            if (keys != null && !keys.isEmpty()) {
                for (String key : keys) {
                    List<String> jsonList = redisTemplate.opsForList().range(key, 0, -1);
                    if (jsonList == null) continue;
                    for (String json : jsonList) {
                        try {
                            AIChatMessage msg = com.alibaba.fastjson2.JSON.parseObject(json, AIChatMessage.class);
                            if (msg == null) continue;
                            // 只取用户发送的文本消息，排除 AI 助手的回复
                            if ("user".equals(msg.getRole()) && "text".equals(msg.getType())) {
                                String content = msg.getContent();
                                if (content != null && !content.isEmpty()) {
                                    // 时间范围过滤
                                    boolean include = true;
                                    if (msg.getTimestamp() != null) {
                                        try {
                                            LocalDateTime msgTime = LocalDateTime.parse(msg.getTimestamp());
                                            if (msgTime.isBefore(start) || msgTime.isAfter(end)) {
                                                include = false;
                                            }
                                        } catch (Exception e) {
                                            log.debug("AI消息时间解析失败，默认包含: {}", msg.getTimestamp());
                                            include = true;
                                        }
                                    }
                                    if (include) {
                                        allTexts.add(content);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            log.warn("解析AI消息失败，json: {}", json, e);
                        }
                    }
                }
            } else {
                log.warn("未找到 Redis 中的 AI 对话消息，请检查键名模式: {}", AI_CHAT_KEY_PREFIX + "*:text");
            }
        } catch (Exception e) {
            log.error("获取AI对话消息失败", e);
        }

        // 2. 从 MySQL 获取用户评论
        try {
            List<String> comments = commentMapper.selectContentByTimeRange(start, end);
            allTexts.addAll(comments);
            log.info("从评论表获取到 {} 条文本", comments.size());
        } catch (Exception e) {
            log.error("获取用户评论失败", e);
        }

        // 3. 从 Redis 获取论坛留言（按时间范围）
        try {
            List<ForumMessage> messages = forumService.getMessagesByTimeRange(start, end, 1000);
            for (ForumMessage msg : messages) {
                if (msg.getContent() != null && !msg.getContent().isEmpty()) {
                    allTexts.add(msg.getContent());
                }
            }
            log.info("从论坛留言获取到 {} 条文本", messages.size());
        } catch (Exception e) {
            log.error("获取论坛留言失败", e);
        }

        log.info("词云总共收集到 {} 条文本片段", allTexts.size());
        // 4. 提取关键词
        Map<String, Integer> wordFreq = textFilterUtil.extractKeywords(allTexts, limit);
        log.info("提取到 {} 个关键词", wordFreq.size());
        return wordFreq;
    }
}