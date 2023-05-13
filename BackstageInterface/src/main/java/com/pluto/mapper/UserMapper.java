package com.pluto.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pluto.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {


    List<User> selectData(@Param("username") String username,@Param("email") String email,@Param("address") String address);

    List<User> selectWithOutId();

//    @Select("SELECT * FROM `sys_user`")
//    List<User> findAll();
//
//    @Insert("INSERT INTO `sys_user`(username,`password`,nickname,email,phone,address)   " +
//            "VALUES(#{username},#{password},#{nickname},#{email},#{phone},#{address})")
//
////    Boolean update(User user);
//
////    @Select("SELECT * FROM `sys_user` WHERE id=#{id}")
////    User findUserById(Integer id);
//
//    @Delete("DELETE FROM `sys_user` WHERE id=#{id}")
//    Boolean deleteUserById(Integer id);
//
//    @Select("SELECT * FROM `sys_user` where username like concat('%',#{username},'%') limit #{pageNum},#{pageSize}")
//    List<User> selectPage(Integer pageNum, Integer pageSize,String username);
//
//    @Select("SELECT COUNT(*) FROM `sys_user`")
//    int countTotal();
}
