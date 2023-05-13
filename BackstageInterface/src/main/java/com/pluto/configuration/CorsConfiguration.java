package com.pluto.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @ClassName : CorsConfiguration  //类名
 * @Description : 解决跨域请求出错的情况//描述
 * @Author : Pluto //作者
 * @Date: 2023/4/21  22:37
 */
@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:8081") // 设置可被访问后台服务器的地址
                .allowedMethods("GET","POST","PUT","DELETE","HEAD","OPTIONS") //允许任何请求方法
                .allowCredentials(true)
                .maxAge(24 * 60 * 60)
                .allowedHeaders("*"); // 允许任何请求头
    }
}
