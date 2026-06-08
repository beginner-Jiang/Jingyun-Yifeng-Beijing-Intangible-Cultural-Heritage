package org.example.beijing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.beijing.entity.Activity;

import java.util.List;

@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {

    @Select("SELECT * FROM activity WHERE type = #{type}")
    List<Activity> selectByType(@Param("type") String type);

    @Select("SELECT * FROM activity WHERE start_time <= NOW() AND end_time >= NOW()")
    List<Activity> selectOngoing();

    @Update("UPDATE activity SET current_participants = current_participants + 1 WHERE id = #{id} AND current_participants < max_participants")
    int increaseParticipants(@Param("id") Long id);

    @Select("SELECT MAX(id) FROM activity")
    Long selectMaxId();
}