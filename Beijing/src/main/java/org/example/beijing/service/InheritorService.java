package org.example.beijing.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.beijing.entity.Inheritor;

public interface InheritorService extends IService<Inheritor> {

    /**
     * 根据用户ID查询传承人档案
     * @param userId 用户ID
     * @return 传承人
     */
    Inheritor getByUserId(Long userId);
}