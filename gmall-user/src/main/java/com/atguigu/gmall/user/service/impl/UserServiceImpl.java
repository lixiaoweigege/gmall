package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.bean.UserBean;
import com.atguigu.gmall.service.UserService;
import com.atguigu.gmall.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public UserBean getUserById(String memberId) {
        return  userMapper.getUerById(memberId);
    }
}
