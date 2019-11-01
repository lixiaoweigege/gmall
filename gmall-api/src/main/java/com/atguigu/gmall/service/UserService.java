package com.atguigu.gmall.service;


import com.atguigu.gmall.bean.UmsMember;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;

import java.util.List;

public interface UserService {
    UmsMember findUserById(String memberId);

    UmsMember findUserByUsernameAndPwd(UmsMember umsMember);

    void setUserTokenToCache(String token, String id);

    UmsMember verifyToken(String token);


    //根据用户id获取用户的收获地址
    List<UmsMemberReceiveAddress> getReceiveAddressByUserId(String userId);

    UmsMember getUserFromCacheById(String userId);

    UmsMemberReceiveAddress getAddressByAddressId(String addressId);
}
