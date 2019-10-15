package com.atguigu.gmall.item.controller;

import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ItemController {
    @RequestMapping("/{a}.html")
    public String thymeleafTst(HttpServletRequest request, @PathVariable("a") String a){
        request.setAttribute("a",a);
        return "index";
    }
}
