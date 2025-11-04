package com.cya.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.cya.entity.Users;
import com.cya.service.UserService;
import com.cya.util.R;
import com.cya.util.consts.Constants;
import com.cya.util.consts.ConvertUtil;
import com.cya.util.http.CodeEnum;
import com.cya.util.vo.PageOut;
import com.cya.util.ro.PageIn;
import com.cya.util.vo.UserOut;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description 用户管理
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/user")
public class UsersController {

    @Autowired
    private UserService userService;

//    用户列表
    @PostMapping("/list")
    public R getUsers(@RequestBody PageIn pageIn) {
        if (pageIn == null) {
            return R.fail(CodeEnum.PARAM_ERROR);
        }
        // 封装分页出参对象
        PageInfo<Users> userList = userService.getUserList(pageIn);
        PageOut pageOut = new PageOut();
        pageOut.setCurrPage(userList.getPageNum());
        pageOut.setPageSize(userList.getPageSize());
        pageOut.setTotal((int) userList.getTotal());
        List<UserOut> outs = new ArrayList<>();
        for (Users users : userList.getList()) {
            UserOut out = new UserOut();
            BeanUtils.copyProperties(users,out);
            out.setIdent(ConvertUtil.identStr(users.getIsAdmin()));
            out.setBirth(DateUtil.format(users.getBirthday(),Constants.DATE_FORMAT));
            outs.add(out);
        }

        pageOut.setList(outs);

        return R.success(CodeEnum.SUCCESS,pageOut);
    }

//    @ApiOperation("添加用户")
//    @PostMapping("/add")
//    public R addUsers(@RequestBody Users users) {
//        return R.success(CodeEnum.SUCCESS,userService.addUser(users));
//    }

//    添加读者
    @PostMapping("/addReader")
    public R addReader(@RequestBody Users users) {
        if (users == null) {
            return R.fail(CodeEnum.PARAM_ERROR);
        }
        return R.success(CodeEnum.SUCCESS,userService.addUser(users));
    }

//    编辑用户
    @PostMapping("/update")
    public R modifyUsers(@RequestBody Users users) {
        return R.success(CodeEnum.SUCCESS,userService.updateUser(users));
    }


//    用户详情
    @GetMapping("/detail")
    public R userDetail(Integer id) {
        Users user = userService.findUserById(id);
        if (user!=null) {
            UserOut out = new UserOut();
            BeanUtils.copyProperties(user,out);
            out.setBirth(DateUtil.format(user.getBirthday(),Constants.DATE_FORMAT));
            out.setIdent(ConvertUtil.identStr(user.getIsAdmin()));
            return R.success(CodeEnum.SUCCESS,out);
        }

        return R.fail(CodeEnum.NOT_FOUND);
    }

//    删除用户
    @GetMapping("/delete")
    public R delUsers(Integer id) {
        userService.deleteUser(id);
        return R.success(CodeEnum.SUCCESS);
    }

//    获取当前用户登陆信息
    @GetMapping("/currUser")
    public R getCurrUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal!=null) {
            Map<String,Object> map = BeanUtil.beanToMap(principal);
            String username = (String) map.get("username");
            if (StrUtil.isNotBlank(username)) {
                Users users = userService.findByUsername(username);
                UserOut out = new UserOut();
                BeanUtils.copyProperties(users,out);
                out.setBirth(DateUtil.format(users.getBirthday(),Constants.DATE_FORMAT));
                Integer isAdmin = users.getIsAdmin();
                String ident = "";
                if (isAdmin == Constants.ADMIN) {
                	ident = Constants.ADMIN_STR;
                }else{
                    ident = Constants.USER_STR;
                }
                out.setIdent(ident);
                return R.success(CodeEnum.SUCCESS,out);
            }
        }
        return R.fail(CodeEnum.USER_NOT_FOUND);
    }

//    用户注册
    @ApiOperation("用户注册")
    @PostMapping("/register")
    public R register(@RequestBody Users users) {
        // 验证参数
        if (users == null || StrUtil.isBlank(users.getUsername()) || StrUtil.isBlank(users.getPassword())) {
            return R.fail(CodeEnum.PARAM_ERROR);
        }
        
        // 检查用户名是否已存在
        Users existUser = userService.findByUsername(users.getUsername());
        if (existUser != null) {
            return R.fail(CodeEnum.USER_EXIST);
        }
        
        // 设置默认值
        users.setIsAdmin(Constants.USER); // 默认为普通用户
        users.setSize(5); // 默认可借5本书
        if (StrUtil.isBlank(users.getNickname())) {
            users.setNickname(users.getUsername());
        }
        
        // 保存用户
        Users result = userService.addUser(users);
        if (result != null) {
            return R.success(CodeEnum.SUCCESS, "注册成功！");
        }
        
        return R.fail(CodeEnum.ERROR);
    }
}
