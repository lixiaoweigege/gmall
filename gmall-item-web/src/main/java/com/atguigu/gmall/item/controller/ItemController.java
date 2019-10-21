package com.atguigu.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.PmsProductSaleAttr;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.bean.PmsSkuSaleAttrValue;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.service.SpuService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ItemController {
    @Reference
    private SkuService skuService;
    @Reference
    private SpuService spuService;

    @RequestMapping("/{skuId}.html")
    public String item(ModelMap map, @PathVariable("skuId") String skuId) {
        //根据库存id查询该商品的库存图片
        PmsSkuInfo pmsSkuInfo = skuService.item(skuId);
        //根据库存id和商品id查询该库存商品对应的销售属性
        String spuId=pmsSkuInfo.getProductId();
        List<PmsProductSaleAttr> pmsProductSaleAttrList=spuService.spuSaleAttrListBySkuIdAndProductId(skuId,spuId);
        map.put("skuInfo", pmsSkuInfo);
        map.put("spuSaleAttrListCheckBySku",pmsProductSaleAttrList);
        //根据当前商品的spuId(productId)查询该商品的所有库存信息
        List<PmsSkuInfo> pmsSkuInfos=skuService.getSkuSaleAttrValueListBySpu(spuId);
        Map<String,String> skuMap = new HashMap<>();
        for (PmsSkuInfo skuInfo : pmsSkuInfos) {
            String v=skuInfo.getId();
            String k="";
            List<PmsSkuSaleAttrValue> pmsSkuSaleAttrValueList=skuInfo.getSkuSaleAttrValueList();
            for (PmsSkuSaleAttrValue pmsSkuSaleAttrValue : pmsSkuSaleAttrValueList) {
                k = k + "|" + pmsSkuSaleAttrValue.getSaleAttrValueId();
            }
            skuMap.put(k ,v);
        }
        String skuJson = JSON.toJSONString(skuMap);

        map.put("skuJson",skuJson);
        map.put("spu","spu_"+spuId+".json");
        return "item";
    }
}
