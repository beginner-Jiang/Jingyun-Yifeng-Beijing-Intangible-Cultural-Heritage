package org.example.beijing.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.beijing.entity.Collection;
import org.example.beijing.mapper.CollectionMapper;
import org.example.beijing.service.CollectionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {

    private final CollectionMapper collectionMapper;

    @Override
    @Transactional
    public void addCollection(Long userId, String targetType, Long targetId) {
        int count = collectionMapper.countByUserAndTarget(userId, targetType, targetId);
        if (count > 0) {
            throw new RuntimeException("已经收藏过了");
        }
        Collection collection = new Collection();
        collection.setUserId(userId);
        collection.setTargetType(targetType);
        collection.setTargetId(targetId);
        collectionMapper.insert(collection);
    }

    @Override
    @Transactional
    public void removeCollection(Long userId, String targetType, Long targetId) {
        int deleted = collectionMapper.deleteByUserAndTarget(userId, targetType, targetId);
        if (deleted == 0) {
            throw new RuntimeException("收藏记录不存在");
        }
    }

    @Override
    public boolean isCollected(Long userId, String targetType, Long targetId) {
        return collectionMapper.countByUserAndTarget(userId, targetType, targetId) > 0;
    }

    @Override
    public List<Map<String, Object>> getItemCollections(Long userId, int limit) {
        return collectionMapper.selectItemCollections(userId, limit);
    }

    @Override
    public List<Map<String, Object>> getSiteCollections(Long userId, int limit) {
        return collectionMapper.selectSiteCollections(userId, limit);
    }

    @Override
    public List<Map<String, Object>> getActivityCollections(Long userId, int limit) {
        return collectionMapper.selectActivityCollections(userId, limit);
    }
}