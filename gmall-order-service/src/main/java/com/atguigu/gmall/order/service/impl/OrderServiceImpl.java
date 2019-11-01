package com.atguigu.gmall.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.OmsOrder;
import com.atguigu.gmall.bean.OmsOrderItem;
import com.atguigu.gmall.bean.UmsMemberReceiveAddress;
import com.atguigu.gmall.order.mapper.OmsOrderItemMapper;
import com.atguigu.gmall.order.mapper.OmsOrderMapper;
import com.atguigu.gmall.service.OrderService;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

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

    @Override
    public void saveTradeCode(String userId, String tradeCode) {
        Jedis jedis = redisUtil.getJedis();
        jedis.setex("user:" + userId + ":tradeCode", 60 * 30, tradeCode);
        jedis.close();
    }

    @Override
    public Boolean checkTradeCode(String userId, String tradeCode) {
        //在需要布尔值判断的时候一般要先把值设为false
        boolean b=false;
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
        Long eval = (Long) jedis.eval(script, Collections.singletonList("user:" + userId + ":tradeCode"),Collections.singletonList(tradeCode));
        if(eval!=null && eval!=0){
            //删除缓存里面的交易码
            jedis.del("user:" + userId + ":tradeCode");
           b=true;
        }else{
            b=false;
        }
    }finally {
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


}
