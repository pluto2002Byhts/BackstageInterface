package com.pluto.configuration.interceptor;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.pluto.common.Constants;
import com.pluto.entity.User;
import com.pluto.exception.ServiceException;
import com.pluto.service.UserService;
import jdk.internal.org.objectweb.asm.Handle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

/**
 * @ClassName : JWTInterceptor  //类名
 * @Description : 拦截token  //描述
 * @Author : Pluto //作者
 * @Date: 2023/5/4  16:22
 */

@Component
public class JWTInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        String token = request.getHeader("token");// 获取请求头的token
        // 如果不是映射到处理器方法上，即请求路径与所有处理器方法的路径都不匹配的时候，直接通过
        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        // 验证token
        if (StrUtil.isBlank(token)){
            throw new ServiceException(Constants.CODE_401, "无token，请重新登录");
        }
        // 获取token中的 userId
        String userId = null;
        try {
            userId = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException e) {
            throw new ServiceException(Constants.CODE_401, "token验证失败");
        }
        User user = userService.getById(userId);
        if (user == null){
            throw new ServiceException(Constants.CODE_401, "用户不存在，请重新登录");
        }
        // 验证令牌签名
        try {
            JWT.require(Algorithm.HMAC256(user.getPassword())).build().verify(token);
        } catch (JWTVerificationException e) {
            throw new ServiceException(Constants.CODE_401, "token验证失败，请重新登录");
        }
        return true;
    }
}
