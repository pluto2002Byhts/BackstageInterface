package com.pluto.configuration;

import com.pluto.configuration.interceptor.JWTInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @ClassName : InterceptorConfig  //类名
 * @Description : 配置拦截器  //描述
 * @Author : Pluto //作者
 * @Date: 2023/5/4  17:20
 */

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Resource
    private JWTInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor) // 注册自定义拦截器
                .addPathPatterns("/**") // 设置拦截路径
                .excludePathPatterns("/**/export","/**/import","/users/login",
                        "/users/register","/users/searchExistUsername","/files/**"); //设置不需要拦截的请求路径
    }
}
