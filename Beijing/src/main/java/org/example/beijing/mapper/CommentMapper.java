package org.example.beijing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.beijing.entity.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {

    @Select("SELECT content FROM comment ORDER BY created_at DESC")
    List<String> selectAllContent();

    @Select("SELECT content FROM comment WHERE created_at BETWEEN #{start} AND #{end} ORDER BY created_at DESC")
    List<String> selectContentByTimeRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);


}