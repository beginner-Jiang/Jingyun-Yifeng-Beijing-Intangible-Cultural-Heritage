package org.example.beijing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.example.beijing.entity.Activity;
import org.example.beijing.entity.ActivitySignup;

import java.util.List;

@Mapper
public interface ActivitySignupMapper extends BaseMapper<ActivitySignup> {

    @Select("SELECT * FROM activity_signup WHERE user_id = #{userId} ORDER BY apply_time DESC")
    List<ActivitySignup> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM activity_signup WHERE user_id = #{userId}")
    int countByUserId(@Param("userId") Long userId);

    @Select("SELECT a.* FROM activity a JOIN activity_signup s ON a.id = s.activity_id WHERE s.user_id = #{userId} ORDER BY s.apply_time DESC")
    List<Activity> selectActivitiesByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM activity_signup WHERE activity_id = #{activityId} ORDER BY apply_time DESC")
    List<ActivitySignup> selectByActivityId(@Param("activityId") Long activityId);

    @Select("SELECT * FROM activity_signup WHERE activity_id = #{activityId} AND user_id = #{userId}")
    ActivitySignup selectByActivityAndUser(@Param("activityId") Long activityId, @Param("userId") Long userId);

    @Update("UPDATE activity_signup SET status = #{status}, process_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}