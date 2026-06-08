package org.example.beijing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.beijing.entity.Collection;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface CollectionMapper extends BaseMapper<Collection> {

    @Select("SELECT * FROM collection WHERE user_id = #{userId} AND target_type = #{targetType} ORDER BY created_at DESC")
    List<Collection> selectByUserAndType(@Param("userId") Long userId, @Param("targetType") String targetType);

    @Select("SELECT COUNT(*) FROM collection WHERE user_id = #{userId} AND target_type = #{targetType} AND target_id = #{targetId}")
    int countByUserAndTarget(@Param("userId") Long userId, @Param("targetType") String targetType, @Param("targetId") Long targetId);

    @Delete("DELETE FROM collection WHERE user_id = #{userId} AND target_type = #{targetType} AND target_id = #{targetId}")
    int deleteByUserAndTarget(@Param("userId") Long userId, @Param("targetType") String targetType, @Param("targetId") Long targetId);

    @Select("SELECT c.id, h.id as targetId, h.name, h.category, h.region, h.image_url as imageUrl " +
            "FROM collection c JOIN heritage_item h ON c.target_id = h.id " +
            "WHERE c.user_id = #{userId} AND c.target_type = 'item' " +
            "ORDER BY c.created_at DESC LIMIT #{limit}")
    List<Map<String, Object>> selectItemCollections(@Param("userId") Long userId, @Param("limit") int limit);

    @Select("SELECT c.id, s.id as targetId, s.name, s.type as category, s.address as region, s.image_url as imageUrl " +
            "FROM collection c JOIN heritage_site s ON c.target_id = s.id " +
            "WHERE c.user_id = #{userId} AND c.target_type = 'site' " +
            "ORDER BY c.created_at DESC LIMIT #{limit}")
    List<Map<String, Object>> selectSiteCollections(@Param("userId") Long userId, @Param("limit") int limit);

    @Select("SELECT c.id, a.id as targetId, a.title as name, a.type as category, '' as region, a.image_url as imageUrl " +
            "FROM collection c JOIN activity a ON c.target_id = a.id " +
            "WHERE c.user_id = #{userId} AND c.target_type = 'activity' " +
            "ORDER BY c.created_at DESC LIMIT #{limit}")
    List<Map<String, Object>> selectActivityCollections(@Param("userId") Long userId, @Param("limit") int limit);

    @Select("SELECT hi.name, COUNT(c.id) as count " +
            "FROM collection c " +
            "JOIN heritage_item hi ON c.target_id = hi.id " +
            "WHERE c.target_type = 'item' AND c.created_at BETWEEN #{start} AND #{end} " +
            "GROUP BY hi.id, hi.name " +
            "ORDER BY count DESC " +
            "LIMIT 10")
    List<Map<String, Object>> selectCountByItem(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Select("SELECT region, COUNT(*) as count " +
            "FROM heritage_item " +
            "WHERE region IS NOT NULL AND region != '' " +
            "GROUP BY region " +
            "ORDER BY count DESC")
    List<Map<String, Object>> selectRegionCount();
}