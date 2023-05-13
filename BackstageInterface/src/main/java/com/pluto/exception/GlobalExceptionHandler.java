package com.pluto.exception;

import com.pluto.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName : GlobalExceptionHandler  //类名
 * @Description : 配置处理异常的组件  //描述
 * @Author : Pluto //作者
 * @Date: 2023/4/29  18:01
 */

@ControllerAdvice   // 此注解将当前类标识为处理异常的组件
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)     //设置该方法可以处理的异常的类型
    @ResponseBody
    public Result handleException(ServiceException se){
        return Result.error(se.getCode(), se.getMessage());
    }

}
