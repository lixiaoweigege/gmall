package com.atguigu.gmall.payment.service.impl;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.atguigu.gmall.bean.PaymentInfo;
import com.atguigu.gmall.payment.mapper.PaymentInfoMapper;
import com.atguigu.gmall.service.PaymentService;
import com.atguigu.gmall.util.ActiveMQUtil;
import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    PaymentInfoMapper paymentInfoMapper;
    @Autowired
    ActiveMQUtil activeMQUtil;
    @Autowired
    AlipayClient alipayClient;

    @Override
    public void savePaymentInfo(PaymentInfo paymentInfo) {
        paymentInfoMapper.insertSelective(paymentInfo);
    }

    @Override
    public void update(PaymentInfo paymentInfo) {
        Example example = new Example(PaymentInfo.class);
        example.createCriteria().andEqualTo("orderSn", paymentInfo.getOrderSn());
        paymentInfoMapper.updateByExampleSelective(paymentInfo, example);
    }

    @Override
    public void sendPaySuccessQueue(PaymentInfo paymentInfo) {
        ConnectionFactory factory = activeMQUtil.getConnectionFactory();
        Connection connection = null;
        try {
            connection = factory.createConnection();
            connection.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        Session session = null;
        try {
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue pay_success_queue = session.createQueue("PAY_SUCCESS_QUEUE");
            MessageProducer producer = session.createProducer(pay_success_queue);
            ActiveMQMapMessage activeMQMapMessage = new ActiveMQMapMessage();
            activeMQMapMessage.setString("out_trade_no", paymentInfo.getOrderSn());
            activeMQMapMessage.setString("status", "success");
            producer.send(activeMQMapMessage);
            session.commit();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendPayCheckQueue(PaymentInfo paymentInfo, long count) {
        ConnectionFactory factory = activeMQUtil.getConnectionFactory();
        Connection connection = null;
        try {
            connection = factory.createConnection();
            connection.start();

        } catch (JMSException e) {
            e.printStackTrace();
        }
        Session session = null;
        try {
            session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue pay_check_queue = session.createQueue("PAY_CHECK_QUEUE");
            MessageProducer producer = session.createProducer(pay_check_queue);
            ActiveMQMapMessage map = new ActiveMQMapMessage();
            map.setString("out_trade_no", paymentInfo.getOrderSn());
            map.setLong("count", count);
            map.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 25 * 1000);
            producer.send(map);
            session.commit();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> checkPay(String out_trade_no) {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        Map<String, Object> mapParam = new HashMap<>();
        mapParam.put("out_trade_no", out_trade_no);
        request.setBizContent(JSON.toJSONString(mapParam));
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        Map<String, Object> map = new HashMap<>();
        if (response.isSuccess()) {
            System.out.println("调用成功");// 交易已创建
            map.put("status", response.getTradeStatus());
            map.put("trade_no", response.getTradeNo());
            map.put("callback_content", JSON.toJSONString(response));
        } else {
            map.put("status", "fail");
            System.out.println("调用失败");// 交易未创建
        }

        return map;
    }

    @Override
    public String checkPayStatus(String out_trade_no) {
        //检查支付状态
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setOrderSn(out_trade_no);
        PaymentInfo paymentInfo1 = paymentInfoMapper.selectOne(paymentInfo);
        return paymentInfo1.getPaymentStatus();
    }

}
