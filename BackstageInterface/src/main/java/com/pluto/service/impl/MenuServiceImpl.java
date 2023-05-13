package com.pluto.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pluto.entity.Menu;
import com.pluto.service.MenuService;
import com.pluto.mapper.MenuMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* @author Pluto
* @description 针对表【sys_menu】的数据库操作Service实现
* @createDate 2023-05-07 18:00:55
*/
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu>
    implements MenuService{

    @Override
    public List<Menu> findMenus(String name) {
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        if(StrUtil.isNotBlank(name)){
            queryWrapper.like("name", name);
        }
        // 查询所有记录
        List<Menu> list = list(queryWrapper);
        // 找出pid为null的一级菜单
        List<Menu> parentNodes = list.stream().filter(menu -> menu.getPid() == null).collect(Collectors.toList());
        // 找出一级菜单的子菜单
        for (Menu parentNode : parentNodes) {
            // 二级菜单为 某个记录中的pid等于parentNode的id
            List<Menu> childrenNodes = list.stream()
                    .filter(menu -> parentNode.getId().equals(menu.getPid())).collect(Collectors.toList());
            parentNode.setChildren(childrenNodes);
        }
        return parentNodes;
    }
}




