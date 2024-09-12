package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @FileName SetmealController
 * @Description 套餐相关接口
 * @Author xb
 * @date 2024-09-12
 **/
@RestController
@RequestMapping("/admin/setmeal")
@Slf4j
@Api(tags ="套餐相关接口")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    /**
    * 新增套餐
    * @param setmealDTO 
    * @return Result 
    * @Date 2024/9/12 01:00
    */
    @PostMapping
    @ApiOperation("新增套餐")
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        log.info("新增套餐:{}", setmealDTO);
        setmealService.saveWithDish(setmealDTO);
        return Result.success();
    }

    /**
    * 套餐分页查询
    * @param setmealPageQueryDTO 
    * @return Result<PageResult> 
    * @Date 2024/9/12 01:45
    */
    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("套餐分页查询:{}", setmealPageQueryDTO);
        PageResult pageResult=setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
    * 批量删除套餐
    * @param ids
    * @return Result
    * @Date 2024/9/12 11:29
    */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    public Result delete(@RequestParam List<Long> ids){
        log.info("批量删除套餐:{}",ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return Result<DishVO>
     * @Date 2024/9/11 21:41
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询菜品")
    public Result<SetmealVO> getById(@PathVariable Long id){
        log.info("根据id查询套餐:{}",id);
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    /**
    * 修改套餐
    * @param setmealDTO
    * @return Result
    * @Date 2024/9/12 12:00
    */
    @PutMapping
    @ApiOperation("修改套餐")
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐:{}",setmealDTO);
        setmealService.updateWithDish(setmealDTO);
        return Result.success();
    }

    /**
    * 套餐启售、停售
    * @param status
     * @param id
    * @return Result
    * @Date 2024/9/12 12:42
    */
    @PostMapping("/status/{status}")
    @ApiOperation("套餐启售、停售")
    public Result startOrStop(@PathVariable("status") Integer status,Long id){
        log.info("套餐起售、停售, id:{}, status:{}", id, status);
        setmealService.startOrStop(status,id);
        return Result.success();
    }
}
