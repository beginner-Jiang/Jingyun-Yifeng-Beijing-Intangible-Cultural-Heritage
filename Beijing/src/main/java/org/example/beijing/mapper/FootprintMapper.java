package org.example.beijing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.beijing.entity.Footprint;

import java.util.List;

@Mapper
public interface FootprintMapper extends BaseMapper<Footprint> {

    /**
     * 查询用户的足迹，按时间倒序
     */
    @Select("SELECT * FROM footprint WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<Footprint> selectByUser(@Param("userId") Long userId);

    /**
     * 查询用户对某类目标的足迹
     */
    @Select("SELECT * FROM footprint WHERE user_id = #{userId} AND target_type = #{targetType} ORDER BY created_at DESC")
    List<Footprint> selectByUserAndType(@Param("userId") Long userId, @Param("targetType") String targetType);
}