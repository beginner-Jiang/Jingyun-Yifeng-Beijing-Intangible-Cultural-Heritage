package org.example.beijing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.beijing.entity.Site;

import java.util.List;
import java.util.Map;

@Mapper
public interface SiteMapper extends BaseMapper<Site> {

    /**
     * 根据类型查询点位
     */
    @Select("SELECT * FROM heritage_site WHERE type = #{type}")
    List<Site> selectByType(String type);

    /**
     * 根据名称模糊搜索
     */
    @Select("SELECT * FROM heritage_site WHERE name LIKE CONCAT('%', #{keyword}, '%')")
    List<Site> searchByName(String keyword);

    /**
     * 查询附近的点位（使用Haversine公式计算距离，返回半径内的点位）
     */
    @Select("SELECT *, " +
            "(6371 * acos(cos(radians(#{lat})) * cos(radians(latitude)) * cos(radians(longitude) - radians(#{lng})) + sin(radians(#{lat})) * sin(radians(latitude)))) AS distance " +
            "FROM heritage_site " +
            "HAVING distance < #{radius} " +
            "ORDER BY distance")
    List<Site> selectNearby(Double lng, Double lat, Integer radius);

    /**
     * 统计各区县的点位数量（按地址前三个字分组）
     */
    @Select("SELECT LEFT(address, 3) as region, COUNT(*) as count FROM heritage_site WHERE address IS NOT NULL GROUP BY LEFT(address, 3)")
    List<Map<String, Object>> selectSiteRegionCount();
}