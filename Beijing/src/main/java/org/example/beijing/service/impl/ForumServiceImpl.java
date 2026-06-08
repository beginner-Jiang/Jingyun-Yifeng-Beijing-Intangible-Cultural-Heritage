package org.example.beijing.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.beijing.entity.Comment;
import org.example.beijing.entity.ForumMessage;
import org.example.beijing.entity.User;
import org.example.beijing.mapper.CommentMapper;
import org.example.beijing.mapper.UserMapper;
import org.example.beijing.service.ForumService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForumServiceImpl implements ForumService {

    private final StringRedisTemplate redisTemplate;
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;

    private static final String FORUM_KEY = "forum:messages";
    private static final int MAX_MESSAGES = 30;

    /**
     * 启动时自动迁移 MySQL 中的历史留言到 Redis
     */
    @PostConstruct
    public void migrateCommentsToRedis() {
        // 检查 Redis 中是否已有数据，避免重复迁移
        Long size = redisTemplate.opsForList().size(FORUM_KEY);
        if (size != null && size > 0) {
            log.info("Redis 中已有留言数据，跳过迁移");
            return;
        }

        log.info("开始将 MySQL comment 表数据迁移到 Redis...");
        // 查询所有评论，按创建时间升序（旧消息在前）
        List<Comment> comments = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .orderByAsc(Comment::getCreatedAt)
        );
        if (comments == null || comments.isEmpty()) {
            log.info("MySQL comment 表无数据，迁移结束");
            return;
        }

        int migratedCount = 0;
        for (Comment comment : comments) {
            ForumMessage message = buildForumMessageFromComment(comment);
            String json = JSON.toJSONString(message);
            // 左推入列表，保持头部为最新消息（因为我们是升序插入，最早的消息会先被 leftPush，最终列表顺序为：最早的在最右，最新的在最左）
            redisTemplate.opsForList().leftPush(FORUM_KEY, json);
            migratedCount++;
        }
        // 保留最近 MAX_MESSAGES 条
        redisTemplate.opsForList().trim(FORUM_KEY, 0, MAX_MESSAGES - 1);
        redisTemplate.expire(FORUM_KEY, 30, TimeUnit.DAYS);
        log.info("迁移完成，共迁移 {} 条留言", migratedCount);
    }

    /**
     * 将 Comment 实体转换为 ForumMessage 对象
     */
    private ForumMessage buildForumMessageFromComment(Comment comment) {
        ForumMessage message = new ForumMessage();
        message.setContent(comment.getContent());
        message.setCreatedAt(comment.getCreatedAt());
        message.setUserId(comment.getUserId());
        User user = userMapper.selectById(comment.getUserId());
        message.setUserName(user != null ? user.getUsername() : "匿名");
        return message;
    }

    /**
     * 获取最近的留言（直接从 Redis 读取）
     */
    @Override
    public List<ForumMessage> getMessages(int limit) {
        List<String> jsonList = redisTemplate.opsForList().range(FORUM_KEY, 0, limit - 1);
        if (jsonList == null) return new ArrayList<>();
        return jsonList.stream()
                .map(json -> JSON.parseObject(json, ForumMessage.class))
                .collect(Collectors.toList());
    }

    /**
     * 发布留言：同时写入 MySQL 和 Redis
     */
    @Override
    @Transactional
    public void postMessage(Long userId, String userName, String content) {
        // 1. 插入 MySQL
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        commentMapper.insert(comment);

        // 2. 构造 ForumMessage 对象
        ForumMessage message = new ForumMessage();
        message.setContent(content);
        message.setCreatedAt(comment.getCreatedAt());
        message.setUserId(userId);
        if (userName == null && userId != null) {
            User user = userMapper.selectById(userId);
            userName = user != null ? user.getUsername() : "匿名";
        } else if (userName == null) {
            userName = "匿名";
        }
        message.setUserName(userName);

        // 3. 写入 Redis 列表头部
        String json = JSON.toJSONString(message);
        redisTemplate.opsForList().leftPush(FORUM_KEY, json);
        redisTemplate.opsForList().trim(FORUM_KEY, 0, MAX_MESSAGES - 1);
        redisTemplate.expire(FORUM_KEY, 30, TimeUnit.DAYS);
    }

    /**
     * 删除用户的所有留言：同时从 MySQL 和 Redis 中删除
     */
    @Override
    @Transactional
    public void deleteUserMessages(Long userId) {
        // 1. 从 MySQL 删除
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getUserId, userId);
        commentMapper.delete(wrapper);

        // 2. 从 Redis 中删除该用户的所有消息（全量刷新方式）
        List<String> jsonList = redisTemplate.opsForList().range(FORUM_KEY, 0, -1);
        if (jsonList == null || jsonList.isEmpty()) return;

        List<String> remaining = jsonList.stream()
                .map(json -> JSON.parseObject(json, ForumMessage.class))
                .filter(msg -> !userId.equals(msg.getUserId()))
                .map(JSON::toJSONString)
                .collect(Collectors.toList());

        redisTemplate.delete(FORUM_KEY);
        if (!remaining.isEmpty()) {
            // 重新推入（保持原有顺序）
            for (int i = remaining.size() - 1; i >= 0; i--) {
                redisTemplate.opsForList().leftPush(FORUM_KEY, remaining.get(i));
            }
            redisTemplate.opsForList().trim(FORUM_KEY, 0, MAX_MESSAGES - 1);
            redisTemplate.expire(FORUM_KEY, 30, TimeUnit.DAYS);
        }
    }

    /**
     * 按时间范围查询留言（用于词云服务，直接从 MySQL 查询，保证数据完整）
     */
    @Override
    public List<ForumMessage> getMessagesByTimeRange(LocalDateTime start, LocalDateTime end, int limit) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(Comment::getCreatedAt, start, end)
                .orderByDesc(Comment::getCreatedAt)
                .last("LIMIT " + limit);
        List<Comment> comments = commentMapper.selectList(wrapper);
        if (comments == null) return new ArrayList<>();
        return comments.stream()
                .map(this::buildForumMessageFromComment)
                .collect(Collectors.toList());
    }
}