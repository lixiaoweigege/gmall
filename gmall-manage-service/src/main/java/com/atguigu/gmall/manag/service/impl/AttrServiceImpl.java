package com.atguigu.gmall.manag.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.PmsBaseAttrInfo;
import com.atguigu.gmall.bean.PmsBaseAttrValue;
import com.atguigu.gmall.manag.mapper.PmsBaseAttrInfoMapper;
import com.atguigu.gmall.manag.mapper.PmsBaseAttrValueMapper;
import com.atguigu.gmall.service.AttrService;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AttrServiceImpl implements AttrService {
    @Autowired
    private PmsBaseAttrInfoMapper pmsBaseAttrInfoMapper;
    @Autowired
    private PmsBaseAttrValueMapper pmsBaseAttrValueMapper;

    public AttrServiceImpl() {
    }

    public List<PmsBaseAttrInfo> attrInfoList(String catalog3Id) {
        PmsBaseAttrInfo pmsBaseAttrInfo = new PmsBaseAttrInfo();
        pmsBaseAttrInfo.setCatalog3Id(catalog3Id);
        List<PmsBaseAttrInfo> pmsBaseAttrInfoList = this.pmsBaseAttrInfoMapper.select(pmsBaseAttrInfo);
        Iterator var4 = pmsBaseAttrInfoList.iterator();

        while(var4.hasNext()) {
            PmsBaseAttrInfo pmsBaseAttrInfo1 = (PmsBaseAttrInfo)var4.next();
            String id = pmsBaseAttrInfo.getId();
            PmsBaseAttrValue pmsBaseAttrValue = new PmsBaseAttrValue();
            pmsBaseAttrValue.setAttrId(id);
            List<PmsBaseAttrValue> pmsBaseAttrValueList = this.pmsBaseAttrValueMapper.select(pmsBaseAttrValue);
            pmsBaseAttrInfo1.setAttrValueList(pmsBaseAttrValueList);
        }

        return pmsBaseAttrInfoList;
    }

    @Override
    public List<PmsBaseAttrInfo> getAttrValueListByValueIds(Set valueIdSet) {
        String join = StringUtils.join(valueIdSet, ",");// 将集合改成，用","分割的字符串
        List<PmsBaseAttrInfo> pmsBaseAttrInfos = pmsBaseAttrValueMapper.selectAttrValueListByValueIds(join);

        return pmsBaseAttrInfos;
    }
}
