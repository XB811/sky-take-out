package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
}
