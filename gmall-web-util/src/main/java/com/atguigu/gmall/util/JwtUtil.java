package com.atguigu.gmall.util;

import io.jsonwebtoken.*;

import java.util.HashMap;
import java.util.Map;

public class JwtUtil {

    public static void main(String[] args) {

        // 服务器密钥

        String serverKey = "gmall0615";

        // 浏览器盐值

        String ip = "127.0.0.1";

        // 用户信息

        Map<String,Object> map = new HashMap<>();
        map.put("userId","1");
        map.put("nickName","tom");

        String encode = encode(serverKey, map, ip);

        System.out.println(encode);

        String token = "eyJhbGciOiJIUzI1NiJ9.eyJuaWNrTmFtZSI6InRvbSIsInVzZXJJZCI6IjEifQ.wVEdbjyEObOhVT6ieIAx2BXD_YB3VU9k0xjpXoihucA";

        // 解密token

        Map<String, Object> decode = decode(token, serverKey, "1273341");

        System.out.println(decode);
    }

    public static String encode(String key, Map<String,Object> param, String salt){
        if(salt!=null){
            key+=salt;
        }
        JwtBuilder jwtBuilder = Jwts.builder().signWith(SignatureAlgorithm.HS256,key);

        jwtBuilder = jwtBuilder.setClaims(param);

        String token = jwtBuilder.compact();
        return token;

    }


    public  static Map<String,Object>  decode(String token ,String key,String salt){
        Claims claims=null;
        if (salt!=null){
            key+=salt;
        }
        try {
            claims= Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        } catch ( JwtException e) {
           return null;
        }
        return  claims;
    }
}
