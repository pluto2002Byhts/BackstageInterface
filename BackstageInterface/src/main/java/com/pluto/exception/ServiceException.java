package com.pluto.exception;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName : ServiceException  //类名
 * @Description : 自定义运行时异常  //描述
 * @Author : Pluto //作者
 * @Date: 2023/4/29  18:04
 */
@Getter
public class ServiceException extends RuntimeException{

    private String code;

    public ServiceException(String code, String message) {
        super(message);
        this.code = code;
    }

}
