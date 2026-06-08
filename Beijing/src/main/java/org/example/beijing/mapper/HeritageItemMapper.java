package org.example.beijing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.beijing.entity.HeritageItem;

import java.util.List;

@Mapper
public interface HeritageItemMapper extends BaseMapper<HeritageItem> {

    /**
     * 根据类别查询项目列表
     */
    @Select("SELECT * FROM heritage_item WHERE category = #{category}")
    List<HeritageItem> selectByCategory(@Param("category") String category);

    /**
     * 获取所有不重复的类别
     */
    @Select("SELECT DISTINCT category FROM heritage_item ORDER BY category")
    List<String> selectAllCategories();
}