package com.atguigu.gmall.manag.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.PmsBaseCatalog1;
import com.atguigu.gmall.bean.PmsBaseCatalog2;
import com.atguigu.gmall.bean.PmsBaseCatalog3;
import com.atguigu.gmall.service.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
@CrossOrigin
@Controller
public class CatalogController {
    @Reference
    private CatalogService catalogServcie;

    @RequestMapping("/getCatalog1")
    @ResponseBody
    public List<PmsBaseCatalog1> getCatalog1(){
       return catalogServcie.getCatalog1();
    }

    @RequestMapping("/getCatalog2")
    @ResponseBody
    public List<PmsBaseCatalog2> getCatalog2(@RequestParam("catalog1Id") String catalog1Id){
        List<PmsBaseCatalog2> pmsBaseCatalog2List=catalogServcie.getCatalog2(catalog1Id);
        return  pmsBaseCatalog2List;
    }
    @ResponseBody
    @RequestMapping("/getCatalog3")
    public  List<PmsBaseCatalog3> getCatalog3(@RequestParam("catalog2Id") String catalog2Id){
        List<PmsBaseCatalog3> pmsBaseCatalog3List=catalogServcie.getCatalog3(catalog2Id);
        return pmsBaseCatalog3List;
    }
}
