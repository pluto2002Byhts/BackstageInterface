package com.pluto.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Quarter;
import com.pluto.common.Result;
import com.pluto.entity.User;
import com.pluto.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName : EchartsController  //类名
 * @Description : echarts控制层  //描述
 * @Author : Pluto //作者
 * @Date: 2023/5/6  22:59
 */

@RestController
@RequestMapping("/echarts")
public class EchartsController {

    @Resource
    private UserService userService;

    @GetMapping("/example")
    public Result get(){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("x", CollUtil.newArrayList("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"));
        map.put("y", CollUtil.newArrayList(200, 230, 224, 218, 135, 147, 260));
        return Result.success(map);
    }

    @GetMapping("/members")
    public Result getMembersData() {
        List<User> list = userService.list();
        int quarter1 = 0;// 第一季度
        int quarter2 = 0;// 第二季度
        int quarter3 = 0;// 第三季度
        int quarter4 = 0;// 第四季度
        for (User user : list) {
            Date createTime = user.getCreateTime();
            Quarter quarter = DateUtil.quarterEnum(createTime);// 根据日期自动匹配一年的四个季度值
            switch (quarter){
                case Q1: quarter1 += 1; break;
                case Q2: quarter2 += 1; break;
                case Q3: quarter3 += 1; break;
                case Q4: quarter4 += 1; break;
                default: break;
            }
        }
        return Result.success(CollUtil.newArrayList(quarter1,quarter2,quarter3, quarter4));
    }

}
