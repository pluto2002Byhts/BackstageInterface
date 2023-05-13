package com.pluto.entity;

import cn.hutool.core.annotation.Alias;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName : User  //类名
 * @Description : 实体类  //描述
 * @Author : Pluto //作者
 * @Date: 2023/4/26  21:57
 */

@Repository
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_user")
@ToString
public class User implements Serializable {

    @TableId
    private Integer id;
    private String username;
//    @JsonIgnore // 设置之后，不会将该属性值暴露给外部
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private String address;
    @TableField("create_time")
    private Date createTime;
    @TableField("head_sculpture_url")
    private String headSculpture;//头像
    private String role;//角色
}
