package com.pluto.mapper;

import com.pluto.entity.RoleMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Pluto
* @description 针对表【sys_role_menu(角色菜单关系表)】的数据库操作Mapper
* @createDate 2023-05-12 12:45:55
* @Entity com.pluto.entity.RoleMenu
*/
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {

    List<Integer> selectByRoleId(@Param("roleId") Integer roleId);
}




