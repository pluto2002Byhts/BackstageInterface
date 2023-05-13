package com.pluto.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.pluto.common.Constants;
import com.pluto.common.Result;
import com.pluto.controller.dto.UserDTO;
import com.pluto.entity.Role;
import com.pluto.entity.User;
import com.pluto.exception.ServiceException;
import com.pluto.mapper.UserMapper;
import com.pluto.service.RoleService;
import com.pluto.service.UserService;
import com.pluto.utils.ServerUtils;
import com.pluto.utils.TokenUtils;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.util.JAXBSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * @ClassName : UserController  //类名
 * @Description : 用户信息控制层-基于mybatis-plus实现增删改查  //描述
 * @Author : Pluto //作者
 * @Date: 2023/4/26  22:15
 */

@RestController
@RequestMapping("/users")
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private RoleService roleService;

    /**
     * 查询所有用户信息
     * @return
     */
    @GetMapping
    public Result getAll(){
        List<User> list = userService.list();
        if (list == null) {
            throw new ServiceException(Constants.CODE_500,"系统错误，请稍后再试");
        }
        return Result.success(list);
    }

    /**
     * 基于mybatis-plus分页插件的分页功能
     * @param pageNum
     * @param pageSize
     * @return IPage<User>
     */
    @GetMapping("/pages/{pageNum}/{pageSize}")
    public Result getPage(
            @PathVariable int pageNum,
            @PathVariable int pageSize
    ){
        IPage<User> iPage = userService.selectPage(pageNum, pageSize);
        if (iPage == null){
            throw new ServiceException(Constants.CODE_500,"系统错误，请稍后再试");
        }
        return Result.success(iPage);
    }

    /**
     * 基于mysql的低效率模糊查询，后面可以整合缓存或者ES分布式查询系统
     * @param username
     * @param email
     * @param address
     * @return Map<String, Object>
     */
    @GetMapping("/search")
    public Result searchData(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String address) {
        List<User> data = null;
        long total = 0;
        try {
            data = userService.searchByCondition(username, email,address);
            total = userService.count();
        } catch (Exception e) {
            throw new ServiceException(Constants.CODE_500,"系统错误，请稍后再试");
        }
        if (data == null) {
            return Result.success(null);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("data", data);
        return Result.success(result);
    }


    /**
     * 根据id查询用户信息
     * @param id
     * @return Result
     */
    @GetMapping("/{id}")
    public Result getById(@PathVariable("id") Integer id){
        User user = userService.getById(id);
        if (user == null) {
            throw new ServiceException(Constants.CODE_500,"系统错误，请稍后再试");
        }
        return Result.success(user);
    }

    /**
     * 插入一条数据
     * @param user
     * @return Boolean
     */
    @PostMapping
    public Result save(@RequestBody User user){
        boolean b = userService.save(user);
        if (!b) {
            throw new ServiceException(Constants.CODE_500,"系统错误，请稍后再试");
        }
        return Result.success();
    }

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    @PutMapping
    public Result update(@RequestBody User user){
        boolean b = userService.updateById(user);
        if (!b) {
            throw new ServiceException(Constants.CODE_500,"系统错误，请稍后再试");
        }
        return Result.success();
    }

    /**
     * 删除一条数据
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable("id") Integer id){
        boolean b = userService.removeById(id);
        if (!b){
            throw new ServiceException(Constants.CODE_500,"系统错误，请稍后再试");
        }
        return Result.success();
    }

    /**
     * 根据多个id实现批量删除数据
     * @param ids
     * @return
     */
    @DeleteMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids){
        boolean b = userService.removeByIds(ids);
        if (!b){
            return Result.error(Constants.CODE_400,"批量删除失败");
        }
        return Result.success();
    }

    /**
     * 导出数据，发起在浏览器下载excel表格文件
     * @return
     * @throws IOException
     */
    @GetMapping("/export")
    public void export(HttpServletResponse response) {
        List<User> list = userService.list();
        try {
            userService.exportData(list,response);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException(Constants.CODE_500,"系统错误，请稍后再试");
        }
    }

    /**
     * 导入数据，前端发送表格文件
     * @return
     * @throws IOException
     */
    @PostMapping("/import")
    public Boolean imPort(@RequestParam("file") MultipartFile file) {
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream(); // 获取输入流
            ExcelReader reader = ExcelUtil.getReader(inputStream);//通过输入流创建ExcelReader 对象
            // 为中文标题设置数据库列名
            reader.addHeaderAlias("用户名","username");
            reader.addHeaderAlias("用户密码","password");
            reader.addHeaderAlias("昵称","nickname");
            reader.addHeaderAlias("邮箱","email");
            reader.addHeaderAlias("电话号码","phone");
            reader.addHeaderAlias("地址","address");
            List<User> list = reader.readAll(User.class);
            userService.saveBatch(list);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 使用UserDTO类来接收前端传过来的用户名和密码
     * @param userDTO
     * @return Result
     */
    @PostMapping("/login")
    public Result login(@RequestBody UserDTO userDTO){
        // 获取用户名和密码
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        // 判断是否为空
        if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
            return Result.error(Constants.CODE_400,"参数错误");
        }
        UserDTO dto = userService.login(userDTO);
        return Result.success(dto);
    }

    /**
     * 注册
     * @return Result
     */
    @PostMapping("/register")
    public Result register(
            @RequestParam(value = "user",required = false) String users,
            @RequestParam(value = "file",required = false) MultipartFile file,
            HttpSession session
            ){
        if(users != null && file != null){
            User user = JSON.parseObject(users,User.class);// 将json字符串转换为JSON对象
            String fileName = file.getOriginalFilename();
            //处理文件重名问题
            String hzName = fileName.substring(fileName.lastIndexOf("."));
            fileName = UUID.randomUUID().toString() + hzName;
            // 获取服务器端口和ip
            String url = ServerUtils.getUrl();
            // 设置头像在数据库的存储路径
            user.setHeadSculpture("http://localhost:8080/headSculpture/"+fileName);

            // 为注册用户设置默认的角色
            QueryWrapper<Role> query = new QueryWrapper<Role>();
            query.eq("flag","ROLE_USER");
            Role role = roleService.getOne(query);
            String roleFlag = role.getFlag();
            user.setRole(roleFlag);
            // 保存用户
            userService.save(user);
            //获取服务器中headSculpture目录的路径
            ServletContext servletContext = session.getServletContext();
            String headSculpturePath = servletContext.getRealPath("headSculpture");
            File fileDir = new File(headSculpturePath);
            if(!fileDir.exists()){
                fileDir.mkdir();
            }
            String finalPath = headSculpturePath + File.separator + fileName;
            //实现上传功能
            try {
                file.transferTo(new File(finalPath));
            } catch (IOException e) {
                throw new ServiceException(Constants.CODE_500,"服务器出错");
            }

        }else {
          return Result.error(Constants.CODE_400,"头像不能为空");
        }
        return Result.success();
    }

    @PostMapping("/searchExistUsername")
    public Result searchUsername(@RequestBody String username){
        System.out.println(username);
        Boolean result = userService.selectUsername(username);
        if(result == false){ // 如果查不出来，则证明用户名没有被注册
            return Result.success(false);
        }else{
            return Result.success(true);
        }
    }

    /**
     * 更新用户信息之后再获取该用户，返回给前端
     * @param user
     * @return Result
     */
    @PutMapping("/updatePersonal")
    public Result updateAndGet(@RequestBody User user){
        boolean result = userService.updateById(user);
        if(result){
            UserDTO userDTO = new UserDTO();
            User u = userService.getById(user.getId());
            BeanUtil.copyProperties(u,userDTO,true);// 将user的部分属性复制给userDTO
            // 生成token
            String token = TokenUtils.getToken(userDTO.getId().toString(), userDTO.getPassword());
            userDTO.setToken(token);
            return Result.success(userDTO);
        }else{
            return Result.error(Constants.CODE_500,"更新失败，请稍后再试");
        }

    }

}
