package com.atguigu.gmall.payment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PaymentController {
    @RequestMapping("/index")
    public String index(){
        return "index";
    }
}
