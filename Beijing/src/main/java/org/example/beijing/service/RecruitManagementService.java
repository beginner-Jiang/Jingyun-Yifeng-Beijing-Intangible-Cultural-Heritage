package org.example.beijing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.beijing.entity.Recruit;

public interface RecruitManagementService extends IService<Recruit> {
    Recruit getRecruitById(Long id);
    void endRecruit(Long recruitId);
}