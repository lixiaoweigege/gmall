package com.atguigu.gmall.item.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ItemController {
    @RequestMapping("/{a}.html")
    public String thymeleafTst(){
        return "index";
    }
}
