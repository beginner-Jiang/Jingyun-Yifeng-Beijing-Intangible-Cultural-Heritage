package org.example.beijing.service;

import java.util.List;
import java.util.Map;

public interface FootprintService {

    void addFootprint(Long userId, String targetType, Long targetId, String action);

    List<Map<String, Object>> getUserFootprints(Long userId, int limit);

    List<Map<String, Object>> getUserFootprints(Long userId);
}