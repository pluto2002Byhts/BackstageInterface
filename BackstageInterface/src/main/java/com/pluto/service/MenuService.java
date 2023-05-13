package com.pluto.service;

import com.pluto.entity.Menu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Pluto
* @description 针对表【sys_menu】的数据库操作Service
* @createDate 2023-05-07 18:00:55
*/
public interface MenuService extends IService<Menu> {

    List<Menu> findMenus(String name);
}
