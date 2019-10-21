package com.atguigu.gmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.PmsProductSaleAttr;
import com.atguigu.gmall.bean.PmsSkuImage;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.service.SkuService;
import com.atguigu.gmall.service.SpuService;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
        String productId=pmsSkuInfo.getProductId();
        List<PmsProductSaleAttr> pmsProductSaleAttrList=spuService.spuSaleAttrListBySkuIdAndProductId(skuId,productId);
        map.put("skuInfo", pmsSkuInfo);
        map.put("spuSaleAttrListCheckBySku",pmsProductSaleAttrList);
        return "item";
    }
}
