package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.PmsBaseAttrInfo;

import java.util.List;
import java.util.Set;

public interface AttrService {
   List<PmsBaseAttrInfo> attrInfoList(String catalog3Id);

    List<PmsBaseAttrInfo> getAttrValueListByValueIds(Set valueIdSet);
}
