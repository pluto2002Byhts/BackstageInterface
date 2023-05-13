package com.pluto.utils;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;

/**
 * @ClassName : TokenUtils  //类名
 * @Description : 生成token的工具类  //描述
 * @Author : Pluto //作者
 * @Date: 2023/5/1  15:49
 */

public class TokenUtils {

    public static String getToken(String userId, String sign){
        return JWT.create().withAudience(userId) //将userId保存到token中，作为载荷
                .withExpiresAt(DateUtil.offsetHour(new Date(),10))   // 设置token两小时后过期
                .sign(Algorithm.HMAC256(sign)); //将password作为token的密钥
    }

}
