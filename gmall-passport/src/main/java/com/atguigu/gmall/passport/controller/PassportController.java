package com.atguigu.gmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.service.UserService;
import com.atguigu.gmall.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.print.DocFlavor;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Member;
import java.util.HashMap;
import java.util.Map;

@Controller
public class PassportController {
    @Reference
    UserService userService;

    @RequestMapping("/index")
    public String index(@RequestParam("ReturnUrl") String ReturnUrl, ModelMap modelMap) {
        modelMap.put("ReturnUrl", ReturnUrl);
        return "index";
    }


    @RequestMapping("/login")
    @ResponseBody
    public String login(HttpServletRequest request, String loginName, String passwd, ModelMap modelMap) {
        String token = "";
        // 调用userService，核对用户密码
        UmsMember umsMember = new UmsMember();
        umsMember.setUsername(loginName);
        umsMember.setPassword(passwd);
        UmsMember umsMember1 = userService.findUserByUsernameAndPwd(umsMember);
        if (umsMember == null) {
            token = "fail";
        }else {
            // 生成token
            // 服务器密钥
            String serverKey = "gmall0615";
            // 浏览器盐值
            String ip = "";
            ip = request.getHeader("x-forward-for");
            if (StringUtils.isBlank(ip)) {
                ip = request.getRemoteAddr();
            }
                //封装用户信息
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("userId", umsMember1.getId());
                userMap.put("nickName", umsMember1.getNickname());
                token = JwtUtil.encode(serverKey, userMap, ip);
                //将token写入缓存
                // 通过后，将token写入缓存
                userService.setUserTokenToCache(token, umsMember1.getId());

        }
        // 返回token
        return token;
    }

    //去中心化单点登录，不需要写入缓存
    @RequestMapping("/login1")
    @ResponseBody
    public String login1(String loginName, String passwd, ModelMap modelMa) {
        //调用userService,核对用户密码

        //生成jwttoken

        //服务器密钥
        String serverKey = "WG";
        //浏览器盐值
        String ip = "192.168.110.110";
        //用户信息
        Map<String, Object> map = new HashMap<>();
        map.put("userId", "1");
        map.put("nickName", "tom");
        String encode = JwtUtil.encode(serverKey, map, ip);
        return encode;
    }

    /**
     * 验证token
     */
    @RequestMapping("/verify")
    @ResponseBody
    public String verify(String token) {
        //调用service层方法验证token是否正确，正确的化话返回member信息
        String s = "";
        Map<String, String> resultMap = new HashMap<>();
        UmsMember umsMember = userService.verifyToken(token);
        if (umsMember != null) {
            // 获取用户信息
            resultMap.put("userId", "1");
            resultMap.put("nickName", "tom");
            resultMap.put("success", "success");
        }else {
            resultMap.put("fail","fail");
        }
        return JSON.toJSONString(resultMap);
    }

}

