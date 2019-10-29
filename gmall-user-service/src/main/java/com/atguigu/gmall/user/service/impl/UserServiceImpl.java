package com.atguigu.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.service.UserService;
import com.atguigu.gmall.user.mapper.UserMapper;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public UmsMember findUserById(String memberId) {
        // return  userMapper.getUerById(memberId);
        UmsMember umsMember = new UmsMember();
        umsMember.setId(memberId);
        System.out.println("SERVICE层");
        return userMapper.selectOne(umsMember);
    }

    @Override
    public UmsMember findUserByUsernameAndPwd(UmsMember umsMember) {

        UmsMember umsMember1 = userMapper.selectOne(umsMember);
        return umsMember1;

    }

    @Override
    public void setUserTokenToCache(String token, String id) {
        UmsMember umsMember = new UmsMember();
        umsMember.setId(id);
        UmsMember umsMember1 = userMapper.selectOne(umsMember);
        Jedis jedis = redisUtil.getJedis();
        jedis.setex("user:" + token + ":token", 60 * 60 * 2, JSON.toJSONString(umsMember1));
        jedis.close();

    }

    @Override
    public UmsMember verifyToken(String token) {
        UmsMember umsMember = null;
        Jedis jedis = redisUtil.getJedis();
        String s = jedis.get("user:" + token + ":token");
        if ((StringUtils.isNotBlank(s))) {
            umsMember = JSON.parseObject(s, UmsMember.class);
        }
        jedis.close();
        return umsMember;
    }
}
