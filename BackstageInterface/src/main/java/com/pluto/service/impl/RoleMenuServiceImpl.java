package com.pluto.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pluto.entity.RoleMenu;
import com.pluto.service.RoleMenuService;
import com.pluto.mapper.RoleMenuMapper;
import org.springframework.stereotype.Service;

/**
* @author Pluto
* @description 针对表【sys_role_menu(角色菜单关系表)】的数据库操作Service实现
* @createDate 2023-05-12 12:45:55
*/
@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu>
    implements RoleMenuService{

}




