package com.atguigu.gmall.service;


import com.atguigu.gmall.bean.UmsMember;

public interface UserService {
    UmsMember findUserById(String memberId);
}
