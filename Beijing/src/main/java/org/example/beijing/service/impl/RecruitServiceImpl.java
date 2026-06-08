package org.example.beijing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.beijing.dto.RecruitDTO;
import org.example.beijing.dto.RecruitUpdateDTO;
import org.example.beijing.entity.RecruitApplication;
import org.example.beijing.entity.RecruitPost;
import org.example.beijing.mapper.RecruitApplicationMapper;
import org.example.beijing.mapper.RecruitPostMapper;
import org.example.beijing.service.CacheService;
import org.example.beijing.service.RecruitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecruitServiceImpl implements RecruitService {

    private final RecruitPostMapper recruitPostMapper;
    private final RecruitApplicationMapper applicationMapper;
    private final CacheService cacheService;

    @Override
    public void addPost(Long inheritorId, RecruitDTO.AddPost dto) {
        RecruitPost post = new RecruitPost();
        post.setInheritorId(inheritorId);
        post.setTitle(dto.getTitle());
        post.setDescription(dto.getDescription());
        post.setInheritorName(dto.getInheritorName());
        post.setApplicantCount(0);
        post.setStatus(0);
        post.setMaxApplicants(dto.getMaxApplicants());
        post.setCreatedAt(LocalDateTime.now());
        recruitPostMapper.insert(post);
        cacheService.delete(cacheService.buildUserPostsKey(inheritorId));
    }

    @Override
    public List<Map<String, Object>> getRecruitList(Long currentUserId) {
        List<RecruitPost> posts = recruitPostMapper.selectList(
                new LambdaQueryWrapper<RecruitPost>().orderByDesc(RecruitPost::getCreatedAt)
        );
        List<Map<String, Object>> result = new ArrayList<>();
        for (RecruitPost post : posts) {
            // 动态统计已通过审核的申请人数
            Long acceptedCount = applicationMapper.selectCount(new LambdaQueryWrapper<RecruitApplication>()
                    .eq(RecruitApplication::getRecruitId, post.getId())
                    .eq(RecruitApplication::getStatus, "accepted"));
            // 动态统计总申请人数（可选，用于调试）
            Long totalCount = applicationMapper.selectCount(new LambdaQueryWrapper<RecruitApplication>()
                    .eq(RecruitApplication::getRecruitId, post.getId()));

            Map<String, Object> map = new HashMap<>();
            map.put("id", post.getId());
            map.put("title", post.getTitle());
            map.put("description", post.getDescription());
            map.put("inheritorName", post.getInheritorName());
            map.put("applicantCount", acceptedCount.intValue());      // 已通过人数
            map.put("totalApplicants", totalCount.intValue());       // 总申请人数（前端可选用）
            map.put("maxApplicants", post.getMaxApplicants());
            map.put("status", post.getStatus());
            map.put("createTime", post.getCreatedAt());

            RecruitApplication app = applicationMapper.selectByRecruitAndUser(post.getId(), currentUserId);
            String userStatus = app == null ? "none" : app.getStatus();
            map.put("userStatus", userStatus);
            map.put("inheritorId", post.getInheritorId());

            result.add(map);
        }
        return result;
    }

    @Override
    @Transactional
    public void applyRecruit(Long recruitId, Long userId) {
        RecruitPost post = recruitPostMapper.selectById(recruitId);
        if (post == null || post.getStatus() != 0) {
            throw new RuntimeException("招募不存在或已结束");
        }
        RecruitApplication existing = applicationMapper.selectByRecruitAndUser(recruitId, userId);
        if (existing != null) {
            throw new RuntimeException("已经申请过此招募");
        }
        RecruitApplication app = new RecruitApplication();
        app.setRecruitId(recruitId);
        app.setUserId(userId);
        app.setStatus("pending");
        app.setApplyTime(LocalDateTime.now());
        applicationMapper.insert(app);
        // 不再更新 recruit_post.applicant_count，因为改为动态统计
        // 但为了兼容性，可更新总申请数字段（可选）
        // post.setApplicantCount(post.getApplicantCount() + 1);
        // recruitPostMapper.updateById(post);

        cacheService.delete(cacheService.buildUserApplicationsKey(userId));
        cacheService.delete(cacheService.buildUserPostsKey(post.getInheritorId()));
    }

    @Override
    @Transactional
    public void approveApplication(Long applicationId, Long inheritorId) {
        RecruitApplication app = applicationMapper.selectById(applicationId);
        if (app == null) {
            throw new RuntimeException("申请不存在");
        }
        RecruitPost post = recruitPostMapper.selectById(app.getRecruitId());
        if (post == null || !post.getInheritorId().equals(inheritorId)) {
            throw new RuntimeException("无权操作");
        }
        if (!"pending".equals(app.getStatus())) {
            throw new RuntimeException("申请已被处理");
        }
        if (post.getStatus() != 0) {
            throw new RuntimeException("招募已结束");
        }

        // 检查已通过人数是否已达上限
        long acceptedCount = applicationMapper.selectCount(new LambdaQueryWrapper<RecruitApplication>()
                .eq(RecruitApplication::getRecruitId, post.getId())
                .eq(RecruitApplication::getStatus, "accepted"));
        if (acceptedCount >= post.getMaxApplicants()) {
            throw new RuntimeException("该招募已满员");
        }

        // 检查用户已参与招募数量（最多2个）
        long userAcceptedCount = applicationMapper.selectCount(new LambdaQueryWrapper<RecruitApplication>()
                .eq(RecruitApplication::getUserId, app.getUserId())
                .eq(RecruitApplication::getStatus, "accepted"));
        if (userAcceptedCount >= 2) {
            throw new RuntimeException("该用户已参与2个招募，无法再接受");
        }

        app.setStatus("accepted");
        app.setProcessTime(LocalDateTime.now());
        applicationMapper.updateById(app);

        cacheService.delete(cacheService.buildUserApplicationsKey(app.getUserId()));
        cacheService.delete(cacheService.buildUserPostsKey(inheritorId));
    }

    @Override
    @Transactional
    public void rejectApplication(Long applicationId, Long inheritorId) {
        RecruitApplication app = applicationMapper.selectById(applicationId);
        if (app == null) {
            throw new RuntimeException("申请不存在");
        }
        RecruitPost post = recruitPostMapper.selectById(app.getRecruitId());
        if (post == null || !post.getInheritorId().equals(inheritorId)) {
            throw new RuntimeException("无权操作");
        }
        if (!"pending".equals(app.getStatus())) {
            throw new RuntimeException("申请已被处理");
        }

        app.setStatus("rejected");
        app.setProcessTime(LocalDateTime.now());
        applicationMapper.updateById(app);

        cacheService.delete(cacheService.buildUserApplicationsKey(app.getUserId()));
        cacheService.delete(cacheService.buildUserPostsKey(inheritorId));
    }

    @Override
    @Transactional
    public void endRecruit(Long recruitId, Long inheritorId) {
        RecruitPost post = recruitPostMapper.selectById(recruitId);
        if (post == null || !post.getInheritorId().equals(inheritorId)) {
            throw new RuntimeException("无权操作或招募不存在");
        }
        post.setStatus(1);
        recruitPostMapper.updateById(post);
        // 结束招募时，删除所有申请记录（可选，保留历史也可不删）
        LambdaQueryWrapper<RecruitApplication> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecruitApplication::getRecruitId, recruitId);
        List<RecruitApplication> apps = applicationMapper.selectList(wrapper);
        for (RecruitApplication app : apps) {
            cacheService.delete(cacheService.buildUserApplicationsKey(app.getUserId()));
        }
        applicationMapper.delete(wrapper);
        cacheService.delete(cacheService.buildUserPostsKey(inheritorId));
    }

    @Override
    @Transactional
    public void updateRecruit(Long recruitId, Long inheritorId, RecruitUpdateDTO dto) {
        RecruitPost post = recruitPostMapper.selectById(recruitId);
        if (post == null) {
            throw new RuntimeException("招募不存在");
        }
        if (!post.getInheritorId().equals(inheritorId)) {
            throw new RuntimeException("只能编辑自己发布的招募");
        }
        post.setTitle(dto.getTitle());
        post.setDescription(dto.getDescription());
        post.setInheritorName(dto.getInheritorName());
        recruitPostMapper.updateById(post);
        cacheService.delete(cacheService.buildUserPostsKey(inheritorId));
    }

    @Override
    public List<RecruitApplication> getRecruitApplications(Long recruitId, Long requesterId) {
        // 任何登录用户都可以查看申请列表
        return applicationMapper.selectByRecruit(recruitId);
    }

    @Override
    public List<Map<String, Object>> getUserApplications(Long userId) {
        String cacheKey = cacheService.buildUserApplicationsKey(userId);
        List<Map<String, Object>> cached = (List<Map<String, Object>>) (List<?>) cacheService.getList(cacheKey, Map.class);
        if (cached != null) {
            return cached;
        }

        List<RecruitApplication> apps = applicationMapper.selectByUser(userId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (RecruitApplication app : apps) {
            RecruitPost post = recruitPostMapper.selectById(app.getRecruitId());
            if (post == null) continue;
            Map<String, Object> map = new HashMap<>();
            map.put("id", app.getId());
            map.put("recruitId", app.getRecruitId());
            map.put("recruitTitle", post.getTitle());
            map.put("status", app.getStatus());
            map.put("applyTime", app.getApplyTime());
            map.put("processTime", app.getProcessTime());
            result.add(map);
        }
        cacheService.setList(cacheKey, result);
        return result;
    }

    @Override
    public List<Map<String, Object>> getUserPosts(Long inheritorId) {
        String cacheKey = cacheService.buildUserPostsKey(inheritorId);
        List<Map<String, Object>> cached = (List<Map<String, Object>>) (List<?>) cacheService.getList(cacheKey, Map.class);
        if (cached != null) {
            return cached;
        }

        List<RecruitPost> posts = recruitPostMapper.selectByInheritor(inheritorId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (RecruitPost post : posts) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", post.getId());
            map.put("title", post.getTitle());
            map.put("description", post.getDescription());
            map.put("inheritorName", post.getInheritorName());
            // 动态统计已通过人数
            Long acceptedCount = applicationMapper.selectCount(new LambdaQueryWrapper<RecruitApplication>()
                    .eq(RecruitApplication::getRecruitId, post.getId())
                    .eq(RecruitApplication::getStatus, "accepted"));
            map.put("applicantCount", acceptedCount.intValue());
            map.put("maxApplicants", post.getMaxApplicants());
            map.put("status", post.getStatus());
            map.put("createTime", post.getCreatedAt());
            result.add(map);
        }
        cacheService.setList(cacheKey, result);
        return result;
    }
}