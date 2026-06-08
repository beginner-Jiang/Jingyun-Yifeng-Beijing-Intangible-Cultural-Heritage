package org.example.beijing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.beijing.entity.HeritageItem;

import java.util.List;

public interface HeritageItemService extends IService<HeritageItem> {

    /**
     * 按类别查询项目列表
     * @param category 类别
     * @return 项目列表
     */
    List<HeritageItem> listByCategory(String category);

    /**
     * 获取所有类别
     * @return 类别列表
     */
    List<String> getAllCategories();

    /**
     * 获取项目详情（包含传承人信息等）
     * @param id 项目ID
     * @return 项目实体
     */
    HeritageItem getDetail(Long id);
}