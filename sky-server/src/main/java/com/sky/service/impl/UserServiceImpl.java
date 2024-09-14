package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @FileName UserServiceImpl
 * @Description
 * @Author xb
 * @date 2024-09-13
 **/
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    WeChatProperties weChatProperties;
    @Autowired
    UserMapper userMapper;
    //微信服务接口地址
    public static final String WX_LOGIN="https://api.weixin.qq.com/sns/jscode2session";

    /**
    * 用户登录
    * @param userLoginDTO 
    * @return User 
    * @Date 2024/9/13 20:21
    */
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        //调用微信接口服务，获得当前用户的openid
        String openid = getOpenid(userLoginDTO.getCode());
        //判断open是否为空
        if(openid==null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //当前微信用户是否为新用户
        User user =userMapper.getByOpenid(openid);
        if(user==null){
            //插入用户
            user=User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        //如果是，自动完成注册
        return user;
    }
    /**
    * 获取openid
    * @param code 
    * @return String 
    * @Date 2024/9/13 20:21
    */
    private String getOpenid(String code){
        Map<String,String> map=new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);
        map.put("grant_type","authorization_code");
        String json = HttpClientUtil.doGet(WX_LOGIN, map);
        JSONObject jsonObject = JSON.parseObject(json);
        String openid = jsonObject.getString("openid");
        return openid;
    }
}
