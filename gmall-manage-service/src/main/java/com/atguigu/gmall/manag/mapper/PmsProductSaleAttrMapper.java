package com.atguigu.gmall.manag.mapper;

import com.atguigu.gmall.bean.PmsProductSaleAttr;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PmsProductSaleAttrMapper extends Mapper<PmsProductSaleAttr> {
    List<PmsProductSaleAttr> spuSaleAttrListBySkuIdAndProductId(@Param("skuId") String skuId, @Param("spuId") String productId);
}
