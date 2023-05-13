package com.pluto.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.log.Log;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pluto.common.Constants;
import com.pluto.common.Result;
import com.pluto.controller.dto.UserDTO;
import com.pluto.entity.Menu;
import com.pluto.entity.User;
import com.pluto.exception.ServiceException;
import com.pluto.mapper.RoleMapper;
import com.pluto.mapper.RoleMenuMapper;
import com.pluto.mapper.UserMapper;
import com.pluto.service.MenuService;
import com.pluto.service.UserService;
import com.pluto.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName : UserServiceImpl  //类名
 * @Description : 实现类  //描述
 * @Author : Pluto //作者
 * @Date: 2023/4/26  22:55
 */

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private RoleMenuMapper roleMenuMapper;

    @Resource
    private MenuService menuService;

    private static final Log LOG = Log.get();// 获取日志对象

    @Override
    public IPage<User> selectPage(int pageNum, int pageSize) {
        Page<User> page = new Page<User>(pageNum, pageSize);
        QueryWrapper<User> queryWrapper = new QueryWrapper<User>();
        queryWrapper.orderByDesc("id"); // 根据id降序排列数据
        Page<User> userPage = userMapper.selectPage(page, queryWrapper);
        return userPage;
    }

    @Override
    public List<User> searchByCondition(String username, String email, String address) {
        return userMapper.selectData(username,email,address);
    }

    @Override
    public Boolean exportData(List<User> list, HttpServletResponse response){
        ServletOutputStream outputStream = null;
        // 创建writer对象
        ExcelWriter writer = ExcelUtil.getWriter(true);
        // 为JavaBean对象，设置标题别名
        writer.addHeaderAlias("username","用户名");
        writer.addHeaderAlias("password","用户密码");
        writer.addHeaderAlias("nickname","昵称");
        writer.addHeaderAlias("email","邮箱");
        writer.addHeaderAlias("phone","电话号码");
        writer.addHeaderAlias("address","地址");
        // 只导出设置了别名的列，这样id就不用导出去了
        writer.setOnlyAlias(true);
        try {
            // 一次性写出list内的对象到excel，使用默认样式，强制输出标题
            writer.write(list, true);
            // 设置浏览器响应的格式
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            // 设置表格文件名
            String fileName = URLEncoder.encode("用户信息","UTF-8");
            // 设置响应头，发起浏览器下载的行为
            response.setHeader("Content-Disposition", "attachment;filename="+fileName+".xls");
            outputStream = response.getOutputStream();
            // 将Excel Workbook刷出到输出流
            writer.flush(outputStream,true);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            // 关闭资源
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            writer.close();
        }
        return true;
    }

    @Override
    public List<User> listWithOutId() {
        List<User> list = userMapper.selectWithOutId();
        return list;
    }

    /**
     * 后台登录验证
     * @param userDTO
     * @return
     */
    @Override
    public UserDTO login(UserDTO userDTO) {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username",userDTO.getUsername())
                .eq("password",userDTO.getPassword());
        User user = null;
        try {
            user = userMapper.selectOne(query);// 查出来一个记录，若有两个相同的记录会抛异常
        } catch (Exception e) {
            LOG.error(e);
            throw new ServiceException(Constants.CODE_500,"系统错误");
        }
        if(user != null){
            BeanUtil.copyProperties(user,userDTO,true);// 将user的部分属性复制给userDTO
            String token = TokenUtils.getToken(userDTO.getId().toString(), userDTO.getPassword());// 传进user的id和password，生成token
            userDTO.setToken(token);
            // 获取user的角色
            String role = userDTO.getRole();
            // 根据role(role的值对应着角色表的flag字段的值)去查询角色表的id
            Integer roleId = roleMapper.selectByFlag(role);
            // 查询出roleId对应的所有menuId
            List<Integer> menuIds = roleMenuMapper.selectByRoleId(roleId);
            // 查询出所有的一级菜单和一级菜单的子菜单。子菜单在一级菜单children数组对象里面
            List<Menu> menus = menuService.findMenus("");
            // 最终的role角色绑定的菜单集合
            List<Menu> roleMenus = new ArrayList<Menu>();
            // 筛选当前用户角色的菜单
            //**********************************
            // 由于只有全选系统管理才能将系统管理对应的id与role的id绑定起来，所以，必须在children长度大于零的情况下，也把children的父级菜单
            // 也一起添加到最终的角色菜单。第二个if语句，不全选的话系统管理对应的id就不会在menuIds，不会出现添加两次系统管理菜单的情况
            for (Menu menu : menus) { //menu一级菜单的元素，child二级菜单的元素
                List<Menu> children = menu.getChildren();
                // 移除二级菜单id不在menuIds集合的数组元素
                boolean isSuccess = children.removeIf(child -> !menuIds.contains(child.getId()));
                if(children.size() != 0 && isSuccess){
                    roleMenus.add(menu);
                }
                if (menuIds.contains(menu.getId())) { // menuIds是否包含了menu的id
                    roleMenus.add(menu);
                }
            }
            userDTO.setMenu(roleMenus);
            return userDTO;
        }else{
            throw new ServiceException(Constants.CODE_600,"用户名或密码错误!");
        }
    }

    /**
     * 根据前端传来的username，来查找数据库的username
     * @param username
     * @return Boolean
     */
    @Override
    public Boolean selectUsername(String username) {
        QueryWrapper<User> query = new QueryWrapper<>();
        query.eq("username", username);
        User user = null;
        try {
            user = userMapper.selectOne(query);
        } catch (Exception e) {
            LOG.error(e);
            throw new ServiceException(Constants.CODE_500,"系统错误");
        }
        if(user!= null){
            return false;
        }else{
            return true;
        }
    }
}
