package com.pluto.controller.dto;

import com.pluto.entity.Menu;
import lombok.Data;

import java.util.List;

/**
 * @ClassName : UserDTO  //类名
 * @Description :   接收前端请求的参数//描述
 * @Author : Pluto //作者
 * @Date: 2023/4/29  11:19
 */
@Data
public class UserDTO {

    private Integer id;
    private String username;
    private String password;
    private String nickname;
    private String headSculpture;
    // token令牌
    private String token;
    // 角色
    private String role;
    // 菜单
    private List<Menu> menu;

}
