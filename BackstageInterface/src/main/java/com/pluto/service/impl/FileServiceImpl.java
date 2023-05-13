package com.pluto.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pluto.entity.Files;
import com.pluto.service.FileService;
import com.pluto.mapper.FileMapper;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author Pluto
* @description 针对表【sys_file】的数据库操作Service实现
* @createDate 2023-05-05 01:47:53
*/
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, Files>
    implements FileService{

    @Resource
    private FileMapper fileMapper;

    @Override
    public Files getByMD5(String md5) {
        QueryWrapper<Files> query = new QueryWrapper<Files>();
        query.eq("md5", md5);
        List<Files> list = fileMapper.selectList(query);
        return list.size() == 0 ? null : list.get(0);
    }

    /**
     * 根据多个id查询
     * @param ids
     * @return List<Files>
     */
    @Override
    public List<Files> getBatch(List<String> ids) {
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id",ids);
        List<Files> list = fileMapper.selectList(queryWrapper);
        return list;
    }
}




