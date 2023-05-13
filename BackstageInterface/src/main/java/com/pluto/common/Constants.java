package com.pluto.common;

/**
 * 设置状态响应码常量
 */
public interface Constants {

    // 服务器响应码
    String CODE_200 = "200";//成功
    String CODE_400 = "400";//前后端参数不一致
    String CODE_401 = "401";//权限不足
    String CODE_500 = "500";//系统错误
    String CODE_600 = "600";//其他业务异常

    // 其他常量
    String DICT_TYPE_ICON = "icon";
}
