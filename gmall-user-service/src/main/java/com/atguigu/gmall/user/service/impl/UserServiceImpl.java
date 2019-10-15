package com.atguigu.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.service.UserService;
import com.atguigu.gmall.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public UmsMember findUserById(String memberId) {
       // return  userMapper.getUerById(memberId);
        UmsMember umsMember=new UmsMember();
        umsMember.setId(memberId);
        System.out.println("SERVICE层");
        return userMapper.selectOne(umsMember);
    }
}
