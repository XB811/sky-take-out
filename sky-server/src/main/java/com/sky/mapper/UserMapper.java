package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

/**
 * @FileName UserMapper
 * @Description
 * @Author xb
 * @date 2024-09-13
 **/
@Mapper
public interface UserMapper {

    /**
    * 根据openid查询用户
    * @param openid 
    * @return User 
    * @Date 2024/9/13 20:10
    */
    @Select("select * from user where openid = #{openid}")
    User getByOpenid(String openid);
    /**
    * 新增用户
    * @param user 
    * @return 
    * @Date 2024/9/13 20:24
    */
    void insert(User user);

    /**
    * 根据id查询用户
    * @param id
    * @return User 
    * @Date 2024/9/14 14:56
    */
    @Select("select * from user where id = #{id}")
    User getById(Long id);

    Integer countByMap(Map map);
}
