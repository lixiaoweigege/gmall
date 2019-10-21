package com.atguigu.gmall.manag.mapper;

import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.bean.PmsSkuSaleAttrValue;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PmsSkuSaleAttrValueMapper extends Mapper<PmsSkuSaleAttrValue> {
    List<PmsSkuInfo> getSkuSaleAttrValueListBySpu(@Param("productId") String productId);
}
