package org.example.beijing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.beijing.entity.Artwork;

import java.util.List;

@Mapper
public interface ArtworkMapper extends BaseMapper<Artwork> {

    @Select("SELECT * FROM artwork WHERE inheritor_id = #{inheritorId} ORDER BY created_at DESC")
    List<Artwork> selectByInheritorId(@Param("inheritorId") Long inheritorId);

    @Select("SELECT * FROM artwork ORDER BY created_at DESC LIMIT #{limit}")
    List<Artwork> selectLatest(@Param("limit") int limit);

    @Select("SELECT MAX(id) FROM artwork")
    Long selectMaxId();
}