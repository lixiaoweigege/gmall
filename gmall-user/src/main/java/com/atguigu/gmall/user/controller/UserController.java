package com.atguigu.gmall.user.controller;


import com.atguigu.gmall.bean.UserBean;
import com.atguigu.gmall.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @RequestMapping("/getUserById")
    @ResponseBody
    public UserBean getUserById(String id) {
        UserBean user=userService.getUserById(id);

        return user;
    }

}
