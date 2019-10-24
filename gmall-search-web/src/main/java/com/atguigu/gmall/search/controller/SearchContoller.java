package com.atguigu.gmall.search.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.*;
import com.atguigu.gmall.service.AttrService;
import com.atguigu.gmall.service.SearchService;
import com.sun.media.sound.MidiOutDeviceProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.*;

@Controller
public class SearchContoller {
    @Reference
    private SearchService searchService;
    @Reference
    AttrService attrService;

    @RequestMapping("/index.html")
    public String indexTest() {
        return "index";
    }

    @RequestMapping("/list.html")
    public String search(PmsSearchParam pmsSearchParam, ModelMap map) throws IOException {
        //搜索属性
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = searchService.search(pmsSearchParam);
        if (pmsSearchSkuInfos!=null&&pmsSearchSkuInfos.size()>0) {
            Set valueIdSet = new HashSet();
            for (PmsSearchSkuInfo pmsSearchSkuInfo : pmsSearchSkuInfos) {
                List<PmsSkuAttrValue> skuAttrValueList = pmsSearchSkuInfo.getSkuAttrValueList();
                for (PmsSkuAttrValue pmsSkuAttrValue : skuAttrValueList) {
                    String valueId = pmsSkuAttrValue.getValueId();
                    valueIdSet.add(valueId);
                }
            }
            //平台属性,包含平台属性子列表，前端会使用循环获得
            List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrService.getAttrValueListByValueIds(valueIdSet);
            String[] valueIds = pmsSearchParam.getValueId();
            // 面包屑
            List<PmsSearchCrumb> pmsSearchCrumbs = new ArrayList<>();
            if (valueIds != null && valueIds.length > 0) {
                for (String valueId : valueIds) {
                    Iterator<PmsBaseAttrInfo> iterator=pmsBaseAttrInfos.iterator();
                    while (iterator.hasNext()){
                        PmsBaseAttrInfo  pmsBaseAttrInfo=iterator.next();
                        List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
                        for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                           /* 比较前端传过来的属性值id与属性列表中的属性是否相同，相同则删除属性列表中的属性,
                            例如选中运行内存未4g的属性值，则运行内存这个属性就没有必要存在了*/
                            if (valueId.equals(pmsBaseAttrValue.getId())){
                                PmsSearchCrumb pmsSearchCrumb = new PmsSearchCrumb();
                                pmsSearchCrumb.setValueId(valueId);
                                pmsSearchCrumb.setValueName(pmsBaseAttrValue.getValueName());
                                pmsSearchCrumb.setUrlParam(getUrlParams(pmsSearchParam,valueId));
                                pmsSearchCrumbs.add(pmsSearchCrumb);
                                iterator.remove();
                            }
                        }
                    }

                }
            }
            map.put("attrValueSelectedList",pmsSearchCrumbs);
            /**
             * 合并到上方以实现根据面包屑往回走
             */
            //搜索条件中的属性在属性列表中删除
            /*if (valueIds != null && valueIds.length > 0){
                for (String valueId : valueIds) {
                    Iterator<PmsBaseAttrInfo> iterator=pmsBaseAttrInfos.iterator();
                    while (iterator.hasNext()){
                        PmsBaseAttrInfo  pmsBaseAttrInfo=iterator.next();
                        List<PmsBaseAttrValue> attrValueList = pmsBaseAttrInfo.getAttrValueList();
                        for (PmsBaseAttrValue pmsBaseAttrValue : attrValueList) {
                           *//* 比较前端传过来的属性值id与属性列表中的属性是否相同，相同则删除属性列表中的属性,
                            例如选中运行内存未4g的属性值，则运行内存这个属性就没有必要存在了*//*
                            if (valueId.equals(pmsBaseAttrValue.getId())){
                                iterator.remove();
                            }
                        }
                    }
                }
            }*/
            map.put("attrList", pmsBaseAttrInfos);
        }



        map.put("urlParam",getUrlParams(pmsSearchParam));
        map.put("skuLsInfoList", pmsSearchSkuInfos);
        return "list";
    }
    //拼接页面的请求地址并返回，以实现前端页面多条件查询
    public String getUrlParams(PmsSearchParam pmsSearchParam,String...valueIdForDelete) {
        String urlParam = "";
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String keyword = pmsSearchParam.getKeyword();
        String[] valueIds = pmsSearchParam.getValueId();
        if (StringUtils.isNotBlank(catalog3Id)) {
            if (StringUtils.isNotBlank(urlParam)) {
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "catalog3Id=" + catalog3Id;
        }
        if (StringUtils.isNotBlank(keyword)) {
            if (StringUtils.isNotBlank(urlParam)) {
                urlParam = urlParam + "&";
            }
            urlParam = urlParam + "keyword=" + keyword;
        }
        if (valueIds != null && valueIds.length > 0) {
            for (String valueId : valueIds) {
                if(valueIdForDelete==null||valueIdForDelete.length==0){
                    urlParam = urlParam + "&valueId="+valueId;
                }else{
                    if(!valueIdForDelete[0].equals(valueId)){
                        urlParam = urlParam + "&valueId="+valueId;
                    }
                }
            }
        }
        return  urlParam;
    }
}


