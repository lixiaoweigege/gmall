package com.atguigu.gmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;
import com.atguigu.gmall.service.UserService;
import com.atguigu.gmall.user.mapper.UmsMemberReceiveAddressMapper;
import com.atguigu.gmall.user.mapper.UserMapper;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.List;


@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    UmsMemberReceiveAddressMapper umsMemberReceiveAddressMapper;
    @Autowired
    RedisUtil redisUtil;

    //通过用户id获取用户信息
    @Override
    public UmsMember findUserById(String memberId) {
        // return  userMapper.getUerById(memberId);
        UmsMember umsMember = new UmsMember();
        umsMember.setId(memberId);
        System.out.println("SERVICE层");
        return userMapper.selectOne(umsMember);
    }

    //根据用户名和密码获取用户，用户登录验证
    @Override
    public UmsMember findUserByUsernameAndPwd(UmsMember umsMember) {

        UmsMember umsMember1 = userMapper.selectOne(umsMember);
        //查询出该用户的所有收货地址，和其它用户信息一起存入缓存，方便以后结算使用
        List<UmsMemberReceiveAddress> receiveAddressList = getReceiveAddressByUserId(umsMember1.getId());
        umsMember1.setUmsMemberReceiveAddresses(receiveAddressList);
        Jedis jedis = redisUtil.getJedis();
        jedis.setex("user:" + umsMember1.getId() + ":info", 60 * 60 * 2, JSON.toJSONString(umsMember1));
        jedis.close();
        return umsMember1;

    }

    //把token存入缓存，方便以后的操作
    @Override
    public void setUserTokenToCache(String token, String id) {
        UmsMember umsMember = new UmsMember();
        umsMember.setId(id);
        UmsMember umsMember1 = userMapper.selectOne(umsMember);

        Jedis jedis = redisUtil.getJedis();

        jedis.setex("user:" + token + ":token", 60 * 60 * 2, JSON.toJSONString(umsMember1));

        jedis.close();


    }

    //验证token是否正确
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

    //根据用户id获取用户的收获地址
    @Override
    public List<UmsMemberReceiveAddress> getReceiveAddressByUserId(String userId) {
        UmsMemberReceiveAddress umsMemberReceiveAddress=new UmsMemberReceiveAddress();
        umsMemberReceiveAddress.setMemberId(userId);
        return umsMemberReceiveAddressMapper.select(umsMemberReceiveAddress);
    }

    @Override
    public UmsMember getUserFromCacheById(String userId) {
        Jedis jedis = redisUtil.getJedis();
        String s = jedis.get("user:" + userId + ":info");
        UmsMember umsMember = JSON.parseObject(s, UmsMember.class);
        return umsMember;
    }

    @Override
    public UmsMemberReceiveAddress getAddressByAddressId(String addressId) {
        UmsMemberReceiveAddress address = new UmsMemberReceiveAddress();
        address.setId(addressId);
        return umsMemberReceiveAddressMapper.selectOne(address);
    }
}
