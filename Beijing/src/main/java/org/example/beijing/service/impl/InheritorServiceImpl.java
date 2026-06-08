package org.example.beijing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.beijing.entity.Inheritor;
import org.example.beijing.mapper.InheritorMapper;
import org.example.beijing.service.InheritorService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InheritorServiceImpl extends ServiceImpl<InheritorMapper, Inheritor> implements InheritorService {

    @Override
    public Inheritor getByUserId(Long userId) {
        return baseMapper.selectByUserId(userId);
    }
}