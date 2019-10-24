package com.atguigu.gmall.manag.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.manag.mapper.*;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.util.RedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    private PmsSkuInfoMapper pmsSkuInfoMapper;
    @Autowired
    private PmsSkuAttrValueMapper pmsSkuAttrValueMapper;
    @Autowired
    private PmsSkuImageMapper pmsSkuImageMapper;
    @Autowired
    private PmsSkuSaleAttrValueMapper pmsSkuSaleAttrValueMapper;
    @Autowired
    RedisUtil redisUtil;

    @Override
    public void saveSkuInfo(PmsSkuInfo pmsSkuInfo) {
// 保存sku，生成主键
        pmsSkuInfoMapper.insertSelective(pmsSkuInfo);
        String skuId = pmsSkuInfo.getId();

        // 保存skuimage
        List<PmsSkuImage> skuImageList = pmsSkuInfo.getSkuImageList();
        for (PmsSkuImage pmsSkuImage : skuImageList) {
            pmsSkuImage.setSkuId(skuId);
            pmsSkuImageMapper.insertSelective(pmsSkuImage);
        }

        // 保存sku销售属性
        List<PmsSkuSaleAttrValue> skuSaleAttrValueList = pmsSkuInfo.getSkuSaleAttrValueList();
        for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : skuSaleAttrValueList) {
            pmsSkuSaleAttrValue.setSkuId(skuId);
            pmsSkuSaleAttrValueMapper.insertSelective(pmsSkuSaleAttrValue);
        }

        // 保存sku平台属性
        List<PmsSkuAttrValue> skuAttrValueList = pmsSkuInfo.getSkuAttrValueList();
        for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
            pmsSkuAttrValue.setSkuId(skuId);
            pmsSkuAttrValueMapper.insertSelective(pmsSkuAttrValue);
        }
    }

    @Override
    public PmsSkuInfo item(String skuId) {
        PmsSkuInfo pmsSkuInfo;
        Jedis jedis = redisUtil.getJedis();
        try {
            //从缓存中获得key值为skou:skuId:info的值，该值由自己决定怎么定义
            String skuInfoJson = jedis.get("skuInfo:" + skuId + ":info");
            if (StringUtils.isBlank(skuInfoJson)) {
                //获得分布式锁
                String uuid = UUID.randomUUID().toString();
               /* SET key value [EX seconds] [PX milliseconds] [NX|XX]
                EX second :设置键的过期时间为second秒 ，PX millisecond :设置键的过期时间为millisecond毫秒 ，NX :只在键不存在时,
                        才对键进行设置操作 ，XX:只在键已经存在时,才对键进行设置操作，SET操作成功完成时,返回OK ,否则返回nil*/
                String OK = jedis.set("skuInfo:" + skuId + ":lock", uuid, "nx", "px", 10000);
                //若返回值未ok则说明设置分布式锁成功
                if (StringUtils.isNotBlank(OK) && OK.equals("OK")) {
                    // 查询db
                    pmsSkuInfo = pmsSkuInfoMapper.selectByPrimaryKey(skuId);// 访问db前需要进行分布式锁的获取
                    PmsSkuImage pmsSkuImage = new PmsSkuImage();
                    pmsSkuImage.setSkuId(skuId);
                    List<PmsSkuImage> pmsSkuImageList = pmsSkuImageMapper.select(pmsSkuImage);
                    pmsSkuInfo.setSkuImageList(pmsSkuImageList);
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //将查询到的结果放入缓存
                    if(pmsSkuInfo!=null){
                    jedis.set("skuInfo:" + skuId + ":info", JSON.toJSONString(pmsSkuInfo));
                }
                //删除分布式锁
               /* String s = jedis.get("skuInfo:" + skuId + ":lock");
                   if(StringUtils.isNotBlank(s)&&s.equals(uuid)){
                       jedis.del("skuInfo:" + skuId + ":lock");}*/
                //使用lua脚本语言删除分布式锁
                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                Object eval = jedis.eval(script, Collections.singletonList("skuInfo:" + skuId + ":lock"), Collections.singletonList(uuid));
            } else {
                // 自旋
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return item(skuId);
            }
        }else {
            // 转化缓存
            pmsSkuInfo = JSON.parseObject(skuInfoJson, PmsSkuInfo.class);
        }
    }finally {
        jedis.close();
    }
        return pmsSkuInfo;

        //没有使用缓存之前直接从数据库查询的代码
        /*PmsSkuInfo pmsSkuInfo = pmsSkuInfoMapper.selectByPrimaryKey(skuId);
        PmsSkuImage pmsSkuImage = new PmsSkuImage();
        pmsSkuImage.setSkuId(skuId);
        List<PmsSkuImage> pmsSkuImageList = pmsSkuImageMapper.select(pmsSkuImage);
        pmsSkuInfo.setSkuImageList(pmsSkuImageList);
        return pmsSkuInfo;*/
    }

    @Override
    public List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId) {
        return pmsSkuSaleAttrValueMapper.getSkuSaleAttrValueListBySpu(productId);
    }

    @Override
    public List<PmsSkuInfo> getALLsku() {
        List<PmsSkuInfo> pmsSkuInfoList = pmsSkuInfoMapper.selectAll();
        //根据库存id查询对应的库存属性值
        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfoList) {
            PmsSkuAttrValue pmsSkuAttrValue = new PmsSkuAttrValue();
            pmsSkuAttrValue.setSkuId(pmsSkuInfo.getId());
            List<PmsSkuAttrValue> pmsSkuAttrValues = pmsSkuAttrValueMapper.select(pmsSkuAttrValue);
            pmsSkuInfo.setSkuAttrValueList(pmsSkuAttrValues);
        }
        return pmsSkuInfoList;
    }
}
