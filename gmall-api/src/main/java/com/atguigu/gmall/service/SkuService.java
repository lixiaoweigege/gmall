package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.PmsSkuInfo;

import java.util.List;
public interface SkuService {
   void saveSkuInfo(PmsSkuInfo pmsSkuInfo);

    PmsSkuInfo item(String skuId);

    List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(String productId);

    List<PmsSkuInfo> getALLsku();

    PmsSkuInfo getSkuInfoById(String productSkuId);
}
