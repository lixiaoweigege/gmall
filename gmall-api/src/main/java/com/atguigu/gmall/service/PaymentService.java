package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.PaymentInfo;

import java.util.Map;

public interface PaymentService {
    void savePaymentInfo(PaymentInfo paymentInfo);

    void update(PaymentInfo paymentInfo);

    void sendPaySuccessQueue(PaymentInfo paymentInfo);

    void sendPayCheckQueue(PaymentInfo paymentInfo, long count);

    Map<String, Object> checkPay(String out_trade_no);

    String checkPayStatus(String out_trade_no);
}
