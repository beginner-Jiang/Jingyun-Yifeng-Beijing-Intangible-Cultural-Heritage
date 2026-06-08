package org.example.beijing.service;

import java.time.LocalDateTime;
import java.util.Map;

public interface WordCloudService {
    /**
     * 获取词云数据，按权重排序
     * @param start 开始时间
     * @param end   结束时间
     * @param limit 最多返回词条数量
     * @return 词频映射 (词 -> 权重)
     */
    Map<String, Integer> getWordCloud(LocalDateTime start, LocalDateTime end, int limit);
}