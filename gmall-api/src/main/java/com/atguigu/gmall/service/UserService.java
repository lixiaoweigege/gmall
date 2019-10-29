package com.atguigu.gmall.service;


import com.atguigu.gmall.bean.UmsMember;

public interface UserService {
    UmsMember findUserById(String memberId);

    UmsMember findUserByUsernameAndPwd(UmsMember umsMember);

    void setUserTokenToCache(String token, String id);

    UmsMember verifyToken(String token);
}
