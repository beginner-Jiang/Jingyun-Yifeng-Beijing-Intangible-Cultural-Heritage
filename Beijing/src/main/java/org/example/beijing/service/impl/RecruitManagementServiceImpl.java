package org.example.beijing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.beijing.entity.Recruit;
import org.example.beijing.mapper.RecruitMapper;
import org.example.beijing.service.RecruitManagementService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecruitManagementServiceImpl extends ServiceImpl<RecruitMapper, Recruit> implements RecruitManagementService {

    @Override
    public Recruit getRecruitById(Long id) {
        return getById(id);
    }

    @Override
    public void endRecruit(Long recruitId) {
        Recruit recruit = getById(recruitId);
        if (recruit != null) {
            recruit.setStatus(1); // 结束
            updateById(recruit);
        }
    }
}