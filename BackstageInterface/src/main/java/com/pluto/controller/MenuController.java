package com.pluto.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pluto.common.Constants;
import com.pluto.common.Result;
import com.pluto.entity.Dict;
import com.pluto.entity.Menu;
import com.pluto.exception.ServiceException;
import com.pluto.service.DictService;
import com.pluto.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName : MenuController  //类名
 * @Description :   //描述
 * @Author : Pluto //作者
 * @Date: 2023/5/7  18:02
 */

@RestController
@RequestMapping("/menu")
public class MenuController {
    
    @Resource
    private MenuService menuService;
    @Resource
    private DictService dictService;

    /**
     * 根据id查询单个记录
     * @param id
     * @return Result
     */
    @GetMapping("/{id}")
    public Result findOne(@PathVariable String id){
        Menu Menu = menuService.getById(id);
        if(Menu == null){
            throw new ServiceException(Constants.CODE_600, "查询失败，请稍后再试");
        }
        return Result.success(Menu);
    }

    /**
     * 查询所有记录
     * @return Result
     */
    @GetMapping
    public Result findAll(@RequestParam(defaultValue = "") String name){
        List<Menu> parentNodes = menuService.findMenus(name);
        // 一级菜单里携带着相对应的二级菜单
        return Result.success(parentNodes);
    }

    /**
     * 查询分页数据
     * @param name
     * @param pageNum
     * @param pageSize
     * @return Result
     */
    @GetMapping("/page")
    public Result findPage(
            @RequestParam String name,
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize
    ){
        QueryWrapper<Menu> query = new QueryWrapper<Menu>();
        query.like("name", name).orderByDesc("id");
        IPage<Menu> page = menuService.page(new Page<Menu>(pageNum,pageSize), query);
        if (page == null) {
            throw new ServiceException(Constants.CODE_600,"查询失败，请稍后再试");
        }
        return Result.success(page);
    }

    /**
     * 根据id删除单个记录
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteOne(@PathVariable String id){
        boolean result = menuService.removeById(id);
        if (!result){
            throw new ServiceException(Constants.CODE_600,"删除失败，请稍后再试");
        }
        return Result.success(true);
    }

    /**
     * 根据数组id批量删除
     * @param ids
     * @return Result
     */
    @DeleteMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<String> ids){
        boolean result = menuService.removeBatchByIds(ids);
        if (!result){
            throw new ServiceException(Constants.CODE_600,"删除失败，请稍后再试");
        }
        return Result.success(true);
    }

    /**
     * 保存一条记录
     * @param Menu
     * @return Result
     */
    @PostMapping
    public Result saveOne(@RequestBody Menu Menu){
        boolean result = menuService.save(Menu);
        if (!result){
            throw new ServiceException(Constants.CODE_600,"添加失败，请稍后再试");
        }
        return Result.success(true);
    }

    /**
     * 更新一条记录
     * @param Menu
     * @return Result
     */
    @PutMapping
    public Result update(@RequestBody Menu Menu){

        boolean result = menuService.updateById(Menu);
        if (!result){
            throw new ServiceException(Constants.CODE_600,"更新失败，请稍后再试");
        }
        return Result.success(true);
    }

    /**
     * 查询所有的icon图标
     * @return Result
     */
    @GetMapping("/icons")
    public Result getIcon(){
        QueryWrapper<Dict> query = new QueryWrapper<Dict>();
        query.eq("type", Constants.DICT_TYPE_ICON);
        // 根据字典表的type的类型查找记录
        List<Dict> dicts = dictService.list(query);
        if (dicts.size() == 0)  throw new ServiceException(Constants.CODE_600,"查询失败，请稍后再试");
        return Result.success(dicts);
    }
}
