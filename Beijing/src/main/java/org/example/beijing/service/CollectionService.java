package org.example.beijing.service;

import java.util.List;
import java.util.Map;

public interface CollectionService {
    void addCollection(Long userId, String targetType, Long targetId);
    void removeCollection(Long userId, String targetType, Long targetId);
    boolean isCollected(Long userId, String targetType, Long targetId);
    List<Map<String, Object>> getItemCollections(Long userId, int limit);
    List<Map<String, Object>> getSiteCollections(Long userId, int limit);
    List<Map<String, Object>> getActivityCollections(Long userId, int limit);
}