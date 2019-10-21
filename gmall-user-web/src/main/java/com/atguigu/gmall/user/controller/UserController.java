package com.atguigu.gmall.user.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.bean.UserBean;
import com.atguigu.gmall.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class UserController {
    @Reference
    private UserService userService;
    @RequestMapping("/getUserById")
    @ResponseBody
    public UmsMember getUserById(String id) {
        UmsMember user=userService.findUserById(id);
        //6666
        System.out.println("WEB层");
        return user;
    }

}
