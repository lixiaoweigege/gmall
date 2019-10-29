package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.anotations.LoginRequired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class OrderController {
    @LoginRequired(isNeededSuccess = true)
    @RequestMapping("/toTrade")
    public  String toTrade(){
        return "trade";
    }
}
