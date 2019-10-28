package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.OmsCartItem;

import java.util.List;

public interface CartService {
    OmsCartItem isCartExists(String userId, OmsCartItem omsCartItem);

    void updateCart(OmsCartItem omsCartItemFromDb);

    void addCart(OmsCartItem omsCartItem);

    List<OmsCartItem> cartList(String userId);

    void updateCartByUserId(OmsCartItem omsCartItem);
}
