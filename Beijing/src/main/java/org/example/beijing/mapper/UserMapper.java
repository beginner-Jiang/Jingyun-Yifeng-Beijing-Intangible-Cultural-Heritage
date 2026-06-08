package org.example.beijing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.beijing.entity.User;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(@Param("username") String username);

    @Select("SELECT * FROM user WHERE phone = #{phone}")
    User selectByPhone(@Param("phone") String phone);

    @Select("SELECT * FROM user WHERE email = #{email}")
    User selectByEmail(@Param("email") String email);

    @Select("SELECT * FROM user WHERE username = #{account} OR email = #{account} OR phone = #{account}")
    User selectByAccount(@Param("account") String account);

    @Select("SELECT MAX(id) FROM user")
    Long selectMaxId();   // 新增：获取当前最大ID

    @Select("SELECT * FROM user WHERE role = 'inheritor'")
    List<User> selectInheritors();
}