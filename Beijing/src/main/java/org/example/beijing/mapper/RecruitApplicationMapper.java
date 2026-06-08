package org.example.beijing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.beijing.entity.RecruitApplication;
import java.util.List;

@Mapper
public interface RecruitApplicationMapper extends BaseMapper<RecruitApplication> {

    @Select("SELECT * FROM recruit_application WHERE recruit_id = #{recruitId}")
    List<RecruitApplication> selectByRecruit(@Param("recruitId") Long recruitId);

    @Select("SELECT * FROM recruit_application WHERE user_id = #{userId} ORDER BY apply_time DESC")
    List<RecruitApplication> selectByUser(@Param("userId") Long userId);

    @Select("SELECT COUNT(*) FROM recruit_application WHERE user_id = #{userId} AND status = 'accepted'")
    int countAcceptedByUser(@Param("userId") Long userId);

    @Select("SELECT * FROM recruit_application WHERE recruit_id = #{recruitId} AND user_id = #{userId}")
    RecruitApplication selectByRecruitAndUser(@Param("recruitId") Long recruitId, @Param("userId") Long userId);
}