package org.example.beijing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.beijing.entity.Inheritor;

@Mapper
public interface InheritorMapper extends BaseMapper<Inheritor> {

    /**
     * 根据用户ID查询传承人档案
     */
    @Select("SELECT * FROM inheritor WHERE user_id = #{userId}")
    Inheritor selectByUserId(@Param("userId") Long userId);
}