package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

/**
 * @FileName UserService
 * @Description
 * @Author xb
 * @date 2024-09-13
 **/

public interface UserService {

    User wxLogin(UserLoginDTO userLoginDTO);
}
