package com.pluto.service;

import com.pluto.entity.Files;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author Pluto
* @description 针对表【sys_file】的数据库操作Service
* @createDate 2023-05-05 01:47:53
*/
public interface FileService extends IService<Files> {

    Files getByMD5(String md5);

    List<Files> getBatch(List<String> ids);
}
