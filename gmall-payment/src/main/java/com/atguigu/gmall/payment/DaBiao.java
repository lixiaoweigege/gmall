package com.atguigu.gmall.payment;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class DaBiao {
    public static void main(String[] args) {
        //创建连接工厂，相当于全国的公路网
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://112.124.46.249:61616");
        try {
            //创建连接，某个具体的连接
            Connection connection = factory.createConnection();
            connection.start();
            //创建会话
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //创建一个队列，相当于车内的某件商品
            Queue queueTset = session.createQueue("playGame");
            //消息的接收者（消费者
            MessageConsumer consumer = session.createConsumer(queueTset);
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    if (message instanceof TextMessage) ;
                    try {
                        String text=((TextMessage) message).getText();
                        System.out.println("大彪已收到："+text);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
