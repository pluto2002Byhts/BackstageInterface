package com.pluto.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pluto.controller.dto.UserDTO;
import com.pluto.entity.User;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface UserService extends IService<User> {

    /**
     * 分页查询+模糊查询
     * @param pageNum
     * @param pageSize
     * @return
     */
    IPage<User> selectPage(int pageNum, int pageSize);


    /**
     * 模糊查询
     * @param address
     * @param email
     * @param address
     * @return
     */
    List<User> searchByCondition(String username, String email, String address);

    /**
     * 导出数据，发起在浏览器下载excel表格文件
     *
     * @return
     */
    Boolean exportData(List<User> list, HttpServletResponse response) throws IOException;

    /**
     * 查询出除了id以外的所有数据
     * @return List<User>
     */
    List<User> listWithOutId();

    UserDTO login(UserDTO userDTO);

    Boolean selectUsername(String username);

}
