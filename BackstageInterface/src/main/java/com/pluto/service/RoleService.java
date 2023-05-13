package com.pluto.service;

import com.pluto.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import io.swagger.models.auth.In;

import java.util.List;

/**
* @author Pluto
* @description 针对表【sys_role】的数据库操作Service
* @createDate 2023-05-07 11:52:40
*/
public interface RoleService extends IService<Role> {

    void setRoleMenu(Integer roleId, List<Integer> menuIds);

    List<Integer> getRoleMenu(Integer roleId);
}
