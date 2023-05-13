package com.pluto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * @ClassName : Result  //类名
 * @Description : 统一返回结果集  //描述
 * @Author : Pluto //作者
 * @Date: 2023/4/29  16:09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {

    private String code;
    private String msg;
    private Object data;

    /**
     * 访问成功，一般用于只需要返回boolean的处理器
     * @return
     */
    @Contract(" -> new")
    public static @NotNull Result success(){
        return new Result(Constants.CODE_200,"Success",null);
    }

    /**
     * 访问成功
     * @return
     */
    public static @NotNull Result success(Object data){
        return new Result(Constants.CODE_200,"",data);
    }

    public static  Result error(String code,String msg){
        return new Result(code,msg,null);
    }

    public static  Result error(){
        return new Result(Constants.CODE_500,"系统错误",null);
    }


}
