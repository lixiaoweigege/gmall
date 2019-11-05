package com.atguigu.gmall.order.mq;

import com.atguigu.gmall.bean.OmsOrder;
import com.atguigu.gmall.service.OrderService;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
@Component
public class OrderMqConsumer {
    @Autowired
    OrderService orderService;
    @JmsListener(containerFactory = "jmsQueueListener",destination = "PAY_SUCCESS_QUEUE")
    public void orderPayConsumer(ActiveMQMapMessage activeMQMapMessage) throws JMSException {
        // 消费代码
        String out_trade_no = activeMQMapMessage.getString("out_trade_no");
        String success = activeMQMapMessage.getString("status");
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setOrderSn(out_trade_no);
        omsOrder.setStatus("1");
        orderService.updateOrder(omsOrder);
        // 发送订单成功队列，库存消费该队列，锁定库存
        orderService.sendOrderPay(omsOrder);

        System.out.println("订单消费PAY_SUCCESS_QUEUE队列");
    }
}
