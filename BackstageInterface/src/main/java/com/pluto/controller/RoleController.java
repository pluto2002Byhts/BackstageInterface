package com.pluto.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pluto.common.Constants;
import com.pluto.common.Result;
import com.pluto.entity.Role;
import com.pluto.exception.ServiceException;
import com.pluto.service.RoleMenuService;
import com.pluto.service.RoleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName : RoleController  //类名
 * @Description :   //描述
 * @Author : Pluto //作者
 * @Date: 2023/5/7  11:54
 */

@RestController
@RequestMapping("/roles")
public class RoleController {

    @Resource
    private RoleService roleService;
    @Resource
    private RoleMenuService roleMenuService;


    /**
     * 根据id查询单个记录
     * @param id
     * @return Result
     */
    @GetMapping("/{id}")
    public Result findOne(@PathVariable String id){
        Role role = roleService.getById(id);
        if(role == null){
            throw new ServiceException(Constants.CODE_600, "查询失败，请稍后再试");
        }
        return Result.success(role);
    }

    /**
     * 查询所有记录
     * @return Result
     */
    @GetMapping
    public Result findAll(){
        List<Role> list = roleService.list();
        if (list.size() == 0){
            throw new ServiceException(Constants.CODE_600,"查询失败，请稍后再试");
        }
        return Result.success(list);
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
        QueryWrapper<Role> query = new QueryWrapper<Role>();
        query.like("name", name).orderByDesc("id");
        IPage<Role> page = roleService.page(new Page<Role>(pageNum,pageSize), query);
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
        boolean result = roleService.removeById(id);
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
        boolean result = roleService.removeBatchByIds(ids);
        if (!result){
            throw new ServiceException(Constants.CODE_600,"删除失败，请稍后再试");
        }
        return Result.success(true);
    }

    /**
     * 保存一条记录
     * @param role
     * @return Result
     */
    @PostMapping
    public Result saveOne(@RequestBody Role role){
        boolean result = false;
        try {
            result = roleService.save(role);
        } catch (Exception e) {
            throw new ServiceException(Constants.CODE_600,"添加失败，请稍后再试");
        }
        if (!result){
            throw new ServiceException(Constants.CODE_600,"添加失败，请稍后再试");
        }
        return Result.success(true);
    }

    /**
     * 更新一条记录
     * @param role
     * @return Result
     */
    @PutMapping
    public Result update(@RequestBody Role role){
        boolean result = roleService.updateById(role);
        if (!result){
            throw new ServiceException(Constants.CODE_600,"更新失败，请稍后再试");
        }
        return Result.success(true);
    }

    /**
     * 绑定角色和菜单的关系
     * @param roleId 角色id
     * @param menuIds 菜单id数组
     * @return Result
     */
    @PostMapping("/roleMenu/{roleId}")
    public Result roleMenu(@PathVariable Integer roleId, @RequestBody List<Integer> menuIds){
        roleService.setRoleMenu(roleId, menuIds);
        return Result.success();
    }

    /**
     * 回显绑定的菜单
     * @param roleId
     * @return Result
     */
    @GetMapping("/roleMenu/{roleId}")
    public Result getRoleMenu(@PathVariable Integer roleId){
        return Result.success(roleService.getRoleMenu(roleId));
    }

}
