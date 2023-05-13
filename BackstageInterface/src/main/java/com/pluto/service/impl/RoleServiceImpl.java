package com.pluto.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pluto.entity.Role;
import com.pluto.entity.RoleMenu;
import com.pluto.mapper.RoleMenuMapper;
import com.pluto.service.RoleService;
import com.pluto.mapper.RoleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Pluto
* @description 针对表【sys_role】的数据库操作Service实现
* @createDate 2023-05-07 11:52:40
*/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role>
    implements RoleService{

    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Override
    @Transactional
    public void setRoleMenu(Integer roleId, List<Integer> menuIds) {
        // 先解除当前角色id所有的绑定关系
        QueryWrapper<RoleMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        roleMenuMapper.delete(queryWrapper);
        // 再把前端传过来的菜单id数组绑定到当前这个角色的id
        for (Integer menuId : menuIds) {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }
    }

    @Override
    public List<Integer> getRoleMenu(Integer roleId) {
       return roleMenuMapper.selectByRoleId(roleId);
    }
}




