package org.example.beijing.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.beijing.entity.Site;
import org.example.beijing.mapper.SiteMapper;
import org.example.beijing.service.SiteService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteServiceImpl extends ServiceImpl<SiteMapper, Site> implements SiteService {

    @Override
    public List<Site> searchSites(String type, String keyword) {
        LambdaQueryWrapper<Site> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(type) && !"all".equals(type)) {
            wrapper.eq(Site::getType, type);
        }
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Site::getName, keyword);
        }
        return list(wrapper);
    }

    @Override
    public List<Site> findNearby(Double lng, Double lat, Integer radius) {
        // 调用 Mapper 中的 SQL 计算距离
        return baseMapper.selectNearby(lng, lat, radius);
    }
}