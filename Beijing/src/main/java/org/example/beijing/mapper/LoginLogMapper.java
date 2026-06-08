package org.example.beijing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.beijing.entity.LoginLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface LoginLogMapper extends BaseMapper<LoginLog> {

    @Select("SELECT HOUR(login_time) as hour, COUNT(*) as count " +
            "FROM login_log " +
            "WHERE login_time BETWEEN #{start} AND #{end} " +
            "GROUP BY HOUR(login_time) " +
            "ORDER BY hour")
    List<Map<String, Object>> countByHour(LocalDateTime start, LocalDateTime end);
}