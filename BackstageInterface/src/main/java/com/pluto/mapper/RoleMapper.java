package com.pluto.mapper;

import com.pluto.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
* @author Pluto
* @description 针对表【sys_role】的数据库操作Mapper
* @createDate 2023-05-07 11:52:40
* @Entity com.pluto.entity.Role
*/
public interface RoleMapper extends BaseMapper<Role> {

    @Select("select id from sys_role where flag = #{flag}")
    Integer selectByFlag(@Param("flag") String flag);
}




