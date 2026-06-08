package org.example.beijing.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.beijing.entity.ForumMessage;
import org.example.beijing.mapper.CommentMapper;
import org.example.beijing.service.ForumService;
import org.example.beijing.service.WordCloudService;
import org.example.beijing.util.TextFilterUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordCloudServiceImpl implements WordCloudService {

    private final ForumService forumService;      // 获取 AI 对话消息（Redis）
    private final CommentMapper commentMapper;    // 获取用户评论（数据库）
    private final TextFilterUtil textFilterUtil;

    @Override
    public Map<String, Integer> getWordCloud(LocalDateTime start, LocalDateTime end, int limit) {
        List<String> allTexts = new ArrayList<>();

        // 1. 从 Redis 获取 AI 对话消息（假设 ForumService 有根据时间获取的方法）
        try {
            List<ForumMessage> messages = forumService.getMessagesByTimeRange(start, end, 1000);
            for (ForumMessage msg : messages) {
                allTexts.add(msg.getContent());
            }
        } catch (Exception e) {
            log.error("获取 AI 对话消息失败", e);
        }

        // 2. 从数据库获取用户评论（按时间范围）
        try {
            List<String> comments = commentMapper.selectContentByTimeRange(start, end);
            allTexts.addAll(comments);
        } catch (Exception e) {
            log.error("获取用户评论失败", e);
        }

        // 3. 提取关键词
        Map<String, Integer> wordFreq = textFilterUtil.extractKeywords(allTexts, limit);
        return wordFreq;
    }
}