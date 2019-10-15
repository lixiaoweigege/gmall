package com.atguigu.gmall.manag.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.PmsBaseAttrInfo;
import com.atguigu.gmall.bean.PmsBaseAttrValue;
import com.atguigu.gmall.manag.mapper.PmsBaseAttrInfoMapper;
import com.atguigu.gmall.manag.mapper.PmsBaseAttrValueMapper;
import com.atguigu.gmall.service.AttrService;
import java.util.Iterator;
import java.util.List;
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
}
