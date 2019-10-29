package com.atguigu.gmall.interceptors;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.anotations.LoginRequired;
import com.atguigu.gmall.util.CookieUtil;
import com.atguigu.gmall.util.HttpclientUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1、为了防止拦截到不需要拦截的微服务，需要自定义一个注解，只有在加上该注解的方法才拦截
        HandlerMethod hm = (HandlerMethod) handler;
        LoginRequired loginRequired = hm.getMethodAnnotation(LoginRequired.class);
        //如果请求的方法上没有loginrequired注解，则直接放行
        if (loginRequired == null) {
            return true;
        }
        //2、初始化token
        String token = "";
        //假设用户登陆过，从cookie中获得token
        String oldToken = CookieUtil.getCookieValue(request, "oldToken", true);
        //oldToken部不为空，说明用户登录过
        if (StringUtils.isNotBlank(oldToken)) {
            token = oldToken;
        }
        //从请求参数中获得token
        String newToken = request.getParameter("newToken");
        //如果newToken不为空，说明是第一次登录或之前的登录已过期
        if (StringUtils.isNotBlank(newToken)) {
            token = newToken;
        }
        //判断程序运行到此处时，token是否为空，不为空则说明：（1）是第一次登录，
        // （2）之前登陆过，且登录未过期，（3）之前登录过，但是已经过期
        if (StringUtils.isNotBlank(token)) {
            // 远程调用ws接口，rpc，http协议的远程rest风格接口，可以使用http工具
            String result = HttpclientUtil.doGet("http://passport.gmall.com:8085/verify?token=" + token);
            Map<String, String> resultMap = new HashMap<>();
            resultMap = JSON.parseObject(result, resultMap.getClass());
            if (StringUtils.isNotBlank(result) && "success".equals(resultMap.get("success"))) {
                // 验证通过
                // 刷新过期时间(缓存中token的过期时间，cookie中的token的过期时间)
                // 覆盖cookie
                CookieUtil.setCookie(request, response, "oldToken", token, 30 * 60, true);
                // 将用户账户基本信息写入请求
                String userId = resultMap.get("userId");
                String nickName = resultMap.get("nickName");
                request.setAttribute("userId", userId);
                request.setAttribute("nickName", nickName);
                return true;
            } else {
                //验证失败
                //重新进入认证中心，登录
                response.sendRedirect("http://passport.gmall.com:9300/index?ReturnUrl=" + request.getRequestURL());
                return false;
            }
        } else {
            //未登陆过，判断是否需要登录，不需要则直接放行，需要则跳转到登录界面
            if (loginRequired.isNeededSuccess() == false) {
                return true;
            }
            response.sendRedirect("http://passport.gmall.com:8085/index?ReturnUrl=" + request.getRequestURL());
            return false;
        }


    }
}
