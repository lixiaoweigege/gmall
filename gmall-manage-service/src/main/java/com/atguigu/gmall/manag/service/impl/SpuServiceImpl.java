package com.atguigu.gmall.manag.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.PmsBaseSaleAttr;
import com.atguigu.gmall.bean.PmsProductImage;
import com.atguigu.gmall.bean.PmsProductInfo;
import com.atguigu.gmall.bean.PmsProductSaleAttr;
import com.atguigu.gmall.bean.PmsProductSaleAttrValue;
import com.atguigu.gmall.manag.mapper.PmsBaseSaleAttrMapper;
import com.atguigu.gmall.manag.mapper.PmsProductImageMapper;
import com.atguigu.gmall.manag.mapper.PmsProductInfoMapper;
import com.atguigu.gmall.manag.mapper.PmsProductSaleAttrMapper;
import com.atguigu.gmall.manag.mapper.PmsProductSaleAttrValueMapper;
import com.atguigu.gmall.service.SpuService;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class SpuServiceImpl implements SpuService {
    @Autowired
    private PmsProductInfoMapper pmsProductInfoMapper;
    @Autowired
    private PmsBaseSaleAttrMapper pmsBaseSaleAttrMapper;
    @Autowired
    private PmsProductImageMapper pmsProductImageMapper;
    @Autowired
    private PmsProductSaleAttrMapper pmsProductSaleAttrMapper;
    @Autowired
    private PmsProductSaleAttrValueMapper pmsProductSaleAttrValueMapper;

    public SpuServiceImpl() {
    }

    public List<PmsProductInfo> spuList(String catalog3Id) {
        PmsProductInfo pmsProductInfo = new PmsProductInfo();
        pmsProductInfo.setCatalog3Id(catalog3Id);
        List<PmsProductInfo> pmsProductInfoList = this.pmsProductInfoMapper.select(pmsProductInfo);
        return pmsProductInfoList;
    }

    public List<PmsBaseSaleAttr> baseSaleAttrList() {
        List<PmsBaseSaleAttr> pmsBaseSaleAttrList = this.pmsBaseSaleAttrMapper.selectAll();
        return pmsBaseSaleAttrList;
    }

    public void saveSpuInfo(PmsProductInfo pmsProductInfo) {
        this.pmsProductInfoMapper.insertSelective(pmsProductInfo);
        String spuId = pmsProductInfo.getId();
        List<PmsProductImage> pmsProductImageList = pmsProductInfo.getSpuImageList();
        if (pmsProductImageList != null && pmsProductImageList.size() > 0) {
            Iterator var4 = pmsProductImageList.iterator();

            while (var4.hasNext()) {
                PmsProductImage pmsProductImage = (PmsProductImage) var4.next();
                pmsProductImage.setProductId(spuId);
                this.pmsProductImageMapper.insertSelective(pmsProductImage);
            }
        }

        List<PmsProductSaleAttr> pmsProductSaleAttrList = pmsProductInfo.getSpuSaleAttrList();
        Iterator var11 = pmsProductSaleAttrList.iterator();

        while (var11.hasNext()) {
            PmsProductSaleAttr pmsProductSaleAttr = (PmsProductSaleAttr) var11.next();
            pmsProductSaleAttr.setProductId(spuId);
            this.pmsProductSaleAttrMapper.insertSelective(pmsProductSaleAttr);
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValueList = pmsProductSaleAttr.getSpuSaleAttrValueList();
            Iterator var8 = pmsProductSaleAttrValueList.iterator();

            while (var8.hasNext()) {
                PmsProductSaleAttrValue pmsProductSaleAttrValue = (PmsProductSaleAttrValue) var8.next();
                pmsProductSaleAttrValue.setProductId(spuId);
                this.pmsProductSaleAttrValueMapper.insertSelective(pmsProductSaleAttrValue);
            }
        }

    }

    public List<PmsProductSaleAttr> spuSaleAttrList(String productId) {
        PmsProductSaleAttr pmsProductSaleAttr = new PmsProductSaleAttr();
        pmsProductSaleAttr.setProductId(productId);
        List<PmsProductSaleAttr> pmsProductSaleAttrList = this.pmsProductSaleAttrMapper.select(pmsProductSaleAttr);
        Iterator var4 = pmsProductSaleAttrList.iterator();

        while (var4.hasNext()) {
            PmsProductSaleAttr productSaleAttr = (PmsProductSaleAttr) var4.next();
            PmsProductSaleAttrValue pmsProductSaleAttrValue = new PmsProductSaleAttrValue();
            pmsProductSaleAttrValue.setProductId(productId);
            pmsProductSaleAttrValue.setSaleAttrId(productSaleAttr.getSaleAttrId());
            List<PmsProductSaleAttrValue> pmsProductSaleAttrValueList = this.pmsProductSaleAttrValueMapper.select(pmsProductSaleAttrValue);
            productSaleAttr.setSpuSaleAttrValueList(pmsProductSaleAttrValueList);
        }

        return pmsProductSaleAttrList;
    }

    public List<PmsProductImage> spuImageList(String productId) {
        PmsProductImage pmsProductImage = new PmsProductImage();
        pmsProductImage.setProductId(productId);
        return this.pmsProductImageMapper.select(pmsProductImage);
    }

    @Override
    public List<PmsProductSaleAttr> spuSaleAttrListBySkuIdAndProductId(String skuId, String productId) {
        List<PmsProductSaleAttr> pmsProductSaleAttrList = pmsProductSaleAttrMapper.spuSaleAttrListBySkuIdAndProductId(skuId, productId);

        return pmsProductSaleAttrList;
    }
}

