package org.example.beijing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.beijing.entity.RecruitPost;
import java.util.List;

@Mapper
public interface RecruitPostMapper extends BaseMapper<RecruitPost> {

    @Select("SELECT * FROM recruit_post WHERE inheritor_id = #{inheritorId} ORDER BY created_at DESC")
    List<RecruitPost> selectByInheritor(@Param("inheritorId") Long inheritorId);
}