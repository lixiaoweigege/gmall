package com.atguigu.gmall.manag.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.PmsBaseCatalog1;
import com.atguigu.gmall.bean.PmsBaseCatalog2;
import com.atguigu.gmall.bean.PmsBaseCatalog3;
import com.atguigu.gmall.manag.mapper.PmsBaseCatalog1Mapper;
import com.atguigu.gmall.manag.mapper.PmsBaseCatalog2Mapper;
import com.atguigu.gmall.manag.mapper.PmsBaseCatalog3Mapper;
import com.atguigu.gmall.service.CatalogService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CatalogServiceImpl implements CatalogService {
    @Autowired
    private PmsBaseCatalog1Mapper pmsBaseCatalog1Mapper;
    @Autowired
    private PmsBaseCatalog2Mapper pmsBaseCatalog2Mapper;
    @Autowired
    private PmsBaseCatalog3Mapper pmsBaseCatalog3Mapper;

    public CatalogServiceImpl() {
    }

    public List<PmsBaseCatalog1> getCatalog1() {
        return this.pmsBaseCatalog1Mapper.selectAll();
    }

    public List<PmsBaseCatalog2> getCatalog2(String catalog1Id) {
        PmsBaseCatalog2 pmsBaseCatalog2 = new PmsBaseCatalog2();
        pmsBaseCatalog2.setCatalog1Id(catalog1Id);
        return this.pmsBaseCatalog2Mapper.select(pmsBaseCatalog2);
    }

    public List<PmsBaseCatalog3> getCatalog3(String catalog2Id) {
        PmsBaseCatalog3 pmsBaseCatalog3 = new PmsBaseCatalog3();
        pmsBaseCatalog3.setCatalog2Id(catalog2Id);
        return this.pmsBaseCatalog3Mapper.select(pmsBaseCatalog3);
    }
}

