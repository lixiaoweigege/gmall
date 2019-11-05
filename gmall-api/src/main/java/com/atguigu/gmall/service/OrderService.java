package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.OmsOrder;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;

public interface OrderService {
    void saveTradeCode(String userId, String tradeCode);

    Boolean checkTradeCode(String userId, String tradeCode);


    void saveOrder(OmsOrder omsOrder);

    OmsOrder getOrderByUserId(String userId, String orderSn);

    OmsOrder getOrderByOrderSn(String orderSn);

    void updateOrder(OmsOrder omsOrder);

    void sendOrderPay(OmsOrder omsOrder);
}
