package com.atguigu.gmall.payment;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.*;

public class Test {
    public static void main(String[] args) {
        ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://112.124.46.249:61616");
        try {
            //创建连接，相当于创建一条公路
            Connection connection = factory.createConnection();
            connection.start();
            //开启会话，相当于公路上跑的车
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            //队列名称，相当于车内装的货物
            Queue testQueue = session.createQueue("playGame");
            //创建消息的发起者（生产者）
            MessageProducer messageProducer = session.createProducer(testQueue);
            TextMessage textMessage=new ActiveMQTextMessage();
            textMessage.setText("今晚开黑？");//消息内容
            messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
            messageProducer.send(textMessage);
            session.commit();
            session.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
