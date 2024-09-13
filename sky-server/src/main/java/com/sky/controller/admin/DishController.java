package com.sky.controller.admin;

import com.sky.context.BaseContext;
import com.sky.dto.CategoryDTO;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @FileName DishController
 * @Description 菜品管理
 * @Author xb
 * @date 2024-09-11
 **/
@RestController
@RequestMapping("/admin/dish")
@Slf4j
@Api(tags = "菜品相关接口")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
    * 新增菜品
    * @param dishDTO
    * @return Result
    * @Date 2024/9/11 17:09
    */
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品:{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        //清理缓存指定菜品
        cleanCache("dish_"+dishDTO.getCategoryId());
        return Result.success();
    }

    /**
    * 菜品分页查询
    * @param dishPageQueryDTO 
    * @return Result<PageResult> 
    * @Date 2024/9/11 19:21
    */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询:{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
    * 批量删除菜品
    * @param ids 
    * @return Result 
    * @Date 2024/9/11 19:21
    */
    @DeleteMapping
    @ApiOperation("批量删除菜品")
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除菜品:{}",ids);
        dishService.deleteBatch(ids);
        //清理所有的缓存菜品信息
        cleanCache("dish_*");
        return Result.success();
    }

    /**
    * 根据id查询菜品
    * @param id
    * @return Result<DishVO>
    * @Date 2024/9/11 21:41
    */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品:{}",id);
        DishVO dishVO = dishService.getById(id);
        return Result.success(dishVO);
    }

    /**
    * 修改菜品
    * @param dishDTO 
    * @return Result 
    * @Date 2024/9/11 22:17
    */
    @PutMapping
    @ApiOperation("修改菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品:{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);
        //清理所有缓存数据
        cleanCache("dish_*");
        return Result.success();
    }

    /**
    * 菜品起售、停售
    * @param id
     * @param status
    * @return Result
    * @Date 2024/9/11 23:26
    */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品起售、停售")
    public Result startOrStop(@PathVariable("status") Integer status,Long id){
        log.info("菜品起售、停售, id:{}, status:{}", id, status);
        dishService.startOrStop(status,id);
        //清理所有缓存数据
        cleanCache("dish_*");
        return Result.success();
    }
    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<Dish>> list(Long categoryId){
        List<Dish> list = dishService.list(categoryId);
        return Result.success(list);
    }

    //清理缓存数据
    private void cleanCache(String pattern){
        Set keys =redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
