package com.atguigu.gmall.manag.mapper;

import com.atguigu.gmall.bean.PmsBaseAttrInfo;
import com.atguigu.gmall.bean.PmsBaseAttrValue;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface PmsBaseAttrValueMapper extends Mapper<PmsBaseAttrValue> {
    List<PmsBaseAttrInfo> selectAttrValueListByValueIds(@Param("join") String join);
}
