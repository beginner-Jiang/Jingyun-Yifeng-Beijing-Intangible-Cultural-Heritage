package org.example.beijing.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.beijing.entity.HeritageItem;
import org.example.beijing.mapper.HeritageItemMapper;
import org.example.beijing.service.HeritageItemService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HeritageItemServiceImpl extends ServiceImpl<HeritageItemMapper, HeritageItem> implements HeritageItemService {

    @Override
    public List<HeritageItem> listByCategory(String category) {
        if (category == null || category.isEmpty() || "all".equals(category)) {
            return list();
        }
        return baseMapper.selectByCategory(category);
    }

    @Override
    public List<String> getAllCategories() {
        return baseMapper.selectAllCategories();
    }

    @Override
    public HeritageItem getDetail(Long id) {
        HeritageItem item = getById(id);
        if (item == null) {
            throw new RuntimeException("项目不存在");
        }
        // 可以关联加载传承人信息（这里省略）
        return item;
    }
}