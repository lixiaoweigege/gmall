package com.atguigu.gmall.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.bean.PmsBaseAttrInfo;
import com.atguigu.gmall.bean.PmsBaseCatalog1;
import com.atguigu.gmall.bean.PmsSearchParam;
import com.atguigu.gmall.bean.PmsSearchSkuInfo;
import com.atguigu.gmall.service.AttrService;
import com.atguigu.gmall.service.CatalogService;
import com.atguigu.gmall.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(SpringRunner.class)
@SpringBootTest
public  class GmallSearchWebApplicationTests {
    @Reference
    CatalogService catalogService;
    @Reference
    SearchService searchService;
    @Test
    public  void contextLoads() {
    }
    @Test
    public  void  catalogJsonTest() throws IOException {

        List<PmsBaseCatalog1> list= catalogService.getAllCatalogs();
        /*List<PmsBaseCatalog1> list=catalogService.getCatalog1();*/
        File file=new File("C:\\Users\\23132\\Desktop\\io");
        FileOutputStream fileOutputStream=new FileOutputStream(file);
        fileOutputStream.write(JSON.toJSONString(list).getBytes());

    }
    @Test
    public void ServiceImplTest() throws IOException {
        PmsSearchParam p=new PmsSearchParam();
        p.setCatalog3Id("61");
        List<PmsSearchSkuInfo> search = searchService.search(p);
        for (PmsSearchSkuInfo pmsSearchSkuInfo : search) {
            System.out.println(pmsSearchSkuInfo.toString());
        }
    }
    @Reference
    AttrService attrService;
    @Test
    public void getAttrValueListByValueIdsTest(){
        Set valueIdSet = new HashSet();
        valueIdSet.add(12);
        valueIdSet.add(13);
        valueIdSet.add(39);
        valueIdSet.add(41);

        List<PmsBaseAttrInfo> pmsBaseAttrInfos = attrService.getAttrValueListByValueIds(valueIdSet);

        System.out.println( pmsBaseAttrInfos.toString());
    }
}
