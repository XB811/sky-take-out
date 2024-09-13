package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @FileName ShopController
 * @Description
 * @Author xb
 * @date 2024-09-13
 **/
@RestController("userShopController")
@RequestMapping("/user/shop")
@Api(tags = "C端-店铺操作接口")
@Slf4j
public class ShopController {
    @Autowired
    private RedisTemplate redisTemplate;
    public static final String key="SHOP_STATUS";
    @GetMapping("/status")
    @ApiOperation("用户端查询营业状态")
    public Result<Integer> getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(key);
        log.info("用户端查询营业状态:{}",status ==1?"营业中":"打烊中");
        return Result.success(status);
    }
}
