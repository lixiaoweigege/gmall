package com.atguigu.gmall.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.OmsCartItem;
import com.atguigu.gmall.cart.mapper.CartMapper;
import com.atguigu.gmall.service.CartService;
import com.atguigu.gmall.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    CartMapper cartMapper;
    @Autowired
    RedisUtil redisUtil;
    @Override
    public OmsCartItem isCartExists(String userId, OmsCartItem omsCartItem) {
        OmsCartItem omsCartItem1 = new OmsCartItem();
        omsCartItem.setMemberId(userId);
        omsCartItem.setProductAttr(omsCartItem.getProductId());
        OmsCartItem omsCartItem2 = cartMapper.selectOne(omsCartItem);
        return omsCartItem2;
    }

    @Override
    public void updateCart(OmsCartItem omsCartItemFromDb) {
        Example example=new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("id",omsCartItemFromDb.getId());
        cartMapper.updateByExampleSelective(omsCartItemFromDb,example);
        // 同步缓存
        Jedis jedis = redisUtil.getJedis();

        jedis.hset("user:"+omsCartItemFromDb.getMemberId()+":cart",omsCartItemFromDb.getProductSkuId(), JSON.toJSONString(omsCartItemFromDb));

        jedis.close();
    }

    @Override
    public void addCart(OmsCartItem omsCartItem) {
        cartMapper.insertSelective(omsCartItem);
        // 同步缓存
        Jedis jedis = redisUtil.getJedis();

        jedis.hset("user:"+omsCartItem.getMemberId()+":cart",omsCartItem.getProductSkuId(), JSON.toJSONString(omsCartItem));

        jedis.close();
    }

    @Override
    public List<OmsCartItem> cartList(String userId) {
        List<OmsCartItem> omsCartItems = new ArrayList<>();
        // 同步缓存
        Jedis jedis = redisUtil.getJedis();

        List<String> hvals = jedis.hvals("user:" + userId + ":cart");

        if(hvals!=null&&hvals.size()>0){
            // 缓存有
            for (String hval : hvals) {
                OmsCartItem omsCartItem = JSON.parseObject(hval, OmsCartItem.class);
                omsCartItems.add(omsCartItem);
            }
        }else{
            // 缓存没有
            OmsCartItem omsCartItem = new OmsCartItem();
            omsCartItem.setMemberId(userId);
            omsCartItems = cartMapper.select(omsCartItem);

            if(omsCartItems!=null&&omsCartItems.size()>0){
                // 同步缓存
                Map<String,String> map = new HashMap<>();
                for (OmsCartItem cartItem : omsCartItems) {
                    map.put(cartItem.getProductSkuId(),JSON.toJSONString(cartItem));
                }
                jedis.hmset("user:" + userId + ":cart",map);
            }
        }


        jedis.close();

        return omsCartItems;
    }

    @Override
    public void updateCartByUserId(OmsCartItem omsCartItem) {
        Example example=new Example(OmsCartItem.class);
        example.createCriteria().andEqualTo("memberId",omsCartItem.getMemberId()).andEqualTo("productSkuId",omsCartItem.getProductId());
        cartMapper.updateByExampleSelective(omsCartItem,example);
        //同步缓存
        Jedis jedis=redisUtil.getJedis();
        String hget=jedis.hget("user:"+omsCartItem.getMemberId()+":cart",omsCartItem.getProductSkuId());
        OmsCartItem omsCartItem1 = JSON.parseObject(hget, OmsCartItem.class);
        omsCartItem1.setIsChecked(omsCartItem.getIsChecked());
        jedis.hset("user:"+omsCartItem.getMemberId()+":cart",omsCartItem.getProductSkuId(), JSON.toJSONString(omsCartItem1));
        jedis.close();
    }
}
