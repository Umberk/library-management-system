package com.cya.controller;

import com.cya.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Description 用户登录
 */
@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 登录, 使用SpringSecurity 无需自己编写登录接口
     */
//    @ResponseBody
//    @RequestMapping(method = RequestMethod.POST,value = "/user/login")
//    public R login(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String username = request.getParameter("username");
//        String password = request.getParameter("password");
//
//        if (StrUtil.isNotBlank(username)&&StrUtil.isNotBlank(password)) {
//            Users users = userService. login(username, password);
//            if (users!=null) {
//                request.getSession().setAttribute("username",username);
//                return R.success(CodeEnum.SUCCESS);
//            }
//        }
//
//        return R.fail(CodeEnum.NAME_OR_PASS_ERROR);
//    }
    
    /**
     * 跳转到注册页面
     */
    @GetMapping("/toRegister")
    public String toRegister() {
        return "register";
    }
    
}
