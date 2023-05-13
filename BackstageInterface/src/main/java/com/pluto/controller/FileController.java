package com.pluto.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pluto.common.Constants;
import com.pluto.common.Result;
import com.pluto.entity.Files;
import com.pluto.exception.ServiceException;
import com.pluto.service.FileService;
import com.pluto.service.UserService;
import com.pluto.utils.ServerUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * @ClassName : FileController  //类名
 * @Description : 文件上传下载的相关接口  //描述
 * @Author : Pluto //作者
 * @Date: 2023/5/4  23:29
 */

@RestController
@RequestMapping("/files")
public class FileController {

    @Resource
    private FileService fileService;
    

    @Value("${files.upload.path}")
    private String fileUploadPath;

    /**
     * 文件上传接口
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    public String upload(@RequestParam MultipartFile file){
        // 获取文件名
        String fileName = file.getOriginalFilename();
        // 获取文件的后缀，即文件类型
        String type = FileUtil.extName(fileName);
        // 获取文件的大小
        long size = file.getSize();
        // 创建存储文件的目录
        File uploadParentPath = new File(fileUploadPath);
        if (!uploadParentPath.exists()){// 如果路径不存在，就创建这个路径
            uploadParentPath.mkdirs();
        }
        // 定义一个文件唯一的标识码
        String uuid = IdUtil.fastSimpleUUID();
        // 定义最后的文件名
        String fileUUID = uuid+ StrUtil.DOT +type;
        // 定义最后的文件路径
        File uploadFilePath = new File(fileUploadPath +fileUUID );
        // 分析提取文件的md5值
        String md5 = null;
        InputStream is = null;
        String url = null;
        try {
            is = file.getInputStream();// 获取上传文件的输入流
            md5 = SecureUtil.md5(is);// 根据输入文件流来提取解析出MD5
            // 根据md5值去查询数据库中的记录，避免上传重复的文件
            Files files = fileService.getByMD5(md5);
            // 定义文件在数据库存储的url
            url = null;
            // 将获取到的文件存储到磁盘路径
            if (files != null){// 根据md5查找到了文件记录，则证明要上传的文件与数据库表中的文件相同，则不再需要上传文件
                url = files.getUrl();
            }else {
                url = ServerUtils.getUrl()+ "/files/" + fileUUID;
                file.transferTo(uploadFilePath);
            }
        } catch (IOException e) {
            throw new ServiceException(Constants.CODE_500,"服务器出错，请稍后再试");
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                throw new ServiceException(Constants.CODE_500,"服务器出错，请稍后再试");
            }
        }
        // 存储所有文件信息到数据库 
        Files saveFiles = new Files();
        saveFiles.setFileName(fileName);
        saveFiles.setType(type);
        saveFiles.setSize(size/1024);
        saveFiles.setUrl(url);
        saveFiles.setMd5(md5);
        fileService.save(saveFiles);
        return url;
    }

    @GetMapping("/{fileUUID}")
    public void download(@PathVariable String fileUUID, HttpServletResponse response){
        // 根据唯一标识获取服务器的文件
        File uploadFilePath = new File(fileUploadPath +fileUUID );
        ServletOutputStream os = null;//新建输出流对象
        try {
            os = response.getOutputStream();
            // 设置输出流的格式
            response.addHeader("Content-Disposition",
                    "attachment; filename=" + URLEncoder.encode(fileUUID,"UTF-8"));
            response.setContentType("application/octet-stream");
            // 读取文件的字节流
            byte[] bytes = FileUtil.readBytes(uploadFilePath);
            os.write(bytes);
            os.flush();
        } catch (IOException e) {
            throw new ServiceException(Constants.CODE_500,"服务器出错，请稍后再试");
        }finally {
            try {
                os.close();
            } catch (IOException e) {
                throw new ServiceException(Constants.CODE_500,"服务器出错");
            }
        }

    }

    /**
     * 分页查询接口
     * @param pageNum
     * @param pageSize
     * @param fileName
     * @return
     */
    @GetMapping("/pages")
    public Result findPage(
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(defaultValue = "") String fileName){

        QueryWrapper<Files> query = new QueryWrapper<Files>();
        // 只查询未删除的记录
        query.eq("is_deleted",false);
        // 设置降序排序
        query.orderByDesc("id");
        if (!"".equals(fileName)){// 模糊查询
            query.like("file_name",fileName);
        }
        IPage<Files> iPage = fileService.page(new Page(pageNum, pageSize), query);
        return Result.success(iPage);
    }

    /**
     * 实现假删除
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable String id){
        Files files = fileService.getById(id);
        files.setIsDeleted(true);
        boolean result = fileService.updateById(files);
        if (result){
            return Result.success();
        }else {
            return Result.error(Constants.CODE_600,"删除失败");
        }
    }

    /**
     * 实现批量假删除
     * @param ids
     * @return
     */
    @DeleteMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<String> ids){
        if (ids.size() == 0) {
            return Result.error(Constants.CODE_400,"批量删除失败!");
        }else {
            List<Files> list = fileService.getBatch(ids);
            for (Files files : list) {
                files.setIsDeleted(true);
                fileService.updateById(files);
            }
            return Result.success();
        }
    }

    @PutMapping("/update")
    public Result update(@RequestBody Files files){
        boolean result = fileService.updateById(files);
        if (result){
            return Result.success(true);
        }else {
            return Result.error(Constants.CODE_600,"操作失败");
        }
    }


}
