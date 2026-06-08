package org.example.beijing.service;

import org.example.beijing.dto.RecruitDTO;
import org.example.beijing.dto.RecruitUpdateDTO;
import org.example.beijing.entity.RecruitApplication;
import java.util.List;
import java.util.Map;

public interface RecruitService {
    void addPost(Long inheritorId, RecruitDTO.AddPost dto);
    List<Map<String, Object>> getRecruitList(Long currentUserId);
    void applyRecruit(Long recruitId, Long userId);
    void approveApplication(Long applicationId, Long inheritorId);
    void rejectApplication(Long applicationId, Long inheritorId);
    void endRecruit(Long recruitId, Long inheritorId);
    void updateRecruit(Long recruitId, Long inheritorId, RecruitUpdateDTO dto);
    List<RecruitApplication> getRecruitApplications(Long recruitId, Long requesterId);
    List<Map<String, Object>> getUserApplications(Long userId);
    List<Map<String, Object>> getUserPosts(Long inheritorId);
}