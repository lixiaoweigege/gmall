package com.atguigu.gmall.payment.mq;

import com.atguigu.gmall.bean.PaymentInfo;
import com.atguigu.gmall.service.PaymentService;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class PayMqConsumer {
@Autowired
    PaymentService paymentService;
    @JmsListener(containerFactory = "jmsQueueListener",destination = "PAY_CHECK_QUEUE")
    public void orderPayConsumer(ActiveMQMapMessage activeMQMapMessag) throws JMSException {
        String out_trade_no = activeMQMapMessag.getString("out_trade_no");
        Long count = activeMQMapMessag.getLong("count");
        // 调用payService检查out_trade_no订单的支付状态
        Map<String,Object> map = new HashMap<>();
        map = paymentService.checkPay(out_trade_no);
        String status= (String) map.get("status");
        //1.支付成功，不可结束，2.交易结束，不可退款
        if ("TRADE_SUCCESS".equals(status)||"TRADE_FINISHED".equals(status)){
            String pay_status = paymentService.checkPayStatus(out_trade_no);
            if(!pay_status.equals("已支付")) {
                PaymentInfo paymentInfo = new PaymentInfo();
                paymentInfo.setOrderSn(out_trade_no);
                paymentInfo.setPaymentStatus("已支付");
                String trade_no = (String) map.get("trade_no");
                paymentInfo.setAlipayTradeNo(trade_no);
                String callback_content = (String) map.get("callback_content");
                paymentInfo.setCallbackContent(callback_content);
                paymentInfo.setCallbackTime(new Date());
                paymentService.update(paymentInfo);

                // 更新订单信息业务等其他系统业务
                // 发送系统消息队列，通知gmall系统某outTradeNo已经支付成功
                paymentService.sendPaySuccessQueue(paymentInfo);
            }
            System.out.println("检查已经支付，调用支付服务，进行后续系统处理");

        }else{
            //检查不成功，再次发送延迟队列
            if(count>0){
                PaymentInfo paymentInfo = new PaymentInfo();
                paymentInfo.setOrderSn(out_trade_no);
                count --;
                paymentService.sendPayCheckQueue(paymentInfo,count);
                System.out.println("再次发送检查队列，次数剩余"+count+"次");
            }else {
                System.out.println("次数耗尽，停止检查");
            }
        }
        System.out.println("订单消费PAY_SUCCESS_QUEUE队列");
    }
}
