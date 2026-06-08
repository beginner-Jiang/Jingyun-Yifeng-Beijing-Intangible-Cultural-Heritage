package org.example.beijing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.beijing.entity.Site;

import java.util.List;

public interface SiteService extends IService<Site> {

    /**
     * 按类型和关键词搜索点位
     * @param type 类型
     * @param keyword 关键词
     * @return 点位列表
     */
    List<Site> searchSites(String type, String keyword);

    /**
     * 查找附近的点位
     * @param lng 经度
     * @param lat 纬度
     * @param radius 半径（米）
     * @return 点位列表
     */
    List<Site> findNearby(Double lng, Double lat, Integer radius);
}