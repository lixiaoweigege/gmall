package com.atguigu.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.OmsOrder;
import com.atguigu.gmall.bean.OmsOrderItem;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;
import com.atguigu.gmall.order.mapper.OmsOrderItemMapper;
import com.atguigu.gmall.order.mapper.OmsOrderMapper;
import com.atguigu.gmall.service.OrderService;
import com.atguigu.gmall.util.ActiveMQUtil;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.activemq.command.ActiveMQMapMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import javax.jms.*;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    RedisUtil redisUtil;
    @Autowired
    OmsOrderMapper omsOrderMapper;
    @Autowired
    OmsOrderItemMapper omsOrderItemMapper;
    @Autowired
    ActiveMQUtil activeMQUtil;

    @Override
    public void saveTradeCode(String userId, String tradeCode) {
        Jedis jedis = redisUtil.getJedis();
        jedis.setex("user:" + userId + ":tradeCode", 60 * 30, tradeCode);
        jedis.close();
    }

    @Override
    public Boolean checkTradeCode(String userId, String tradeCode) {
        //在需要布尔值判断的时候一般要先把值设为false
        boolean b = false;
        Jedis jedis = redisUtil.getJedis();
        try {
            //1定义一个标识码，用于指定当前用户
            String uuid = UUID.randomUUID().toString();
            //设置分布式锁
            String tradeCodeInCache = jedis.get("user:" + userId + ":tradeCode");
            //多个用户同时提交订单的bug
            //redis lua脚本防止1key多用  防止黑客并发订单攻击
            //对比防重删令牌
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            Long eval = (Long) jedis.eval(script, Collections.singletonList("user:" + userId + ":tradeCode"), Collections.singletonList(tradeCode));
            if (eval != null && eval != 0) {
                //删除缓存里面的交易码
                jedis.del("user:" + userId + ":tradeCode");
                b = true;
            } else {
                b = false;
            }
        } finally {
            jedis.close();
        }
        return b;
    }

    @Override
    public void saveOrder(OmsOrder omsOrder) {
        //保存交易信息，包括收货人的各种信息，并返回交易id
        omsOrderMapper.insertSelective(omsOrder);
        String omsOrderId = omsOrder.getId();
        List<OmsOrderItem> orderItems = omsOrder.getOmsOrderItems();
        for (OmsOrderItem orderItem : orderItems) {
            orderItem.setOrderId(omsOrderId);
            omsOrderItemMapper.insertSelective(orderItem);
        }


    }

    //根据用户id和订单号查询订单
    @Override
    public OmsOrder getOrderByUserId(String userId, String orderSn) {
        //订单
        OmsOrder omsOrder = new OmsOrder();
        omsOrder.setMemberId(userId);
        omsOrder.setOrderSn(orderSn);
        OmsOrder omsOrder1 = omsOrderMapper.selectOne(omsOrder);
        return omsOrder1;
    }

    //根据订单编商品信息号查询订单信息和订单中的
    @Override
    public OmsOrder getOrderByOrderSn(String orderSn) {
//订单信息
        OmsOrder omsOrder=new OmsOrder();
        omsOrder.setOrderSn(orderSn);
        OmsOrder omsOrder1 = omsOrderMapper.selectOne(omsOrder);
        //订单中的商品信息
        OmsOrderItem omsOrderItem=new OmsOrderItem();
        omsOrderItem.setOrderSn(orderSn);
        List<OmsOrderItem> omsOrderItems = omsOrderItemMapper.select(omsOrderItem);
        omsOrder1.setOmsOrderItems(omsOrderItems);
        return omsOrder1;
    }

    @Override
    public void updateOrder(OmsOrder omsOrder) {
        Example example = new Example(OmsOrder.class);
        example.createCriteria().andEqualTo("orderSn",omsOrder.getOrderSn());
        omsOrderMapper.updateByExampleSelective(omsOrder,example);
    }

    @Override
    public void sendOrderPay(OmsOrder omsOrder) {
        // 根据out_trade_no发送延迟检查支付状态队列
        ConnectionFactory connectionFactory = activeMQUtil.getConnectionFactory();

        Connection connection = null;
        try {
            connection = connectionFactory.createConnection();
            connection.start();
        } catch (JMSException e) {
            e.printStackTrace();
        }

        try {
            Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
            Queue pay_success_queue = session.createQueue("ORDER_SUCCESS_QUEUE");
            MessageProducer producer = session.createProducer(pay_success_queue);
            // 消息
            ActiveMQMapMessage map = new ActiveMQMapMessage();
            map.setString("out_trade_no",omsOrder.getOrderSn());
            map.setString("status","1");
            producer.send(map);
            session.commit();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


}
