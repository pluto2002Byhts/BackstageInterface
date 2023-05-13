package com.pluto.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @ClassName : ServerUtils  //类名
 * @Description : 获取ip端口号  //描述
 * @Author : Pluto //作者
 * @Date: 2023/5/5  2:14
 */

@Component
public class ServerUtils {

    @Resource
    Environment environment;

    private static String serverPort;

    private String port ;

    @PostConstruct
    public void setServerPort() {
        serverPort = environment.getProperty("server.port");
    }

    public static String getUrl() {
        String hostAddress = null;
        try {
            hostAddress = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "http://" + hostAddress + ":" + ServerUtils.serverPort;
    }
}
