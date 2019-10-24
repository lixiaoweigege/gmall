package com.atguigu.gmall.service;

import com.atguigu.gmall.bean.PmsSearchParam;
import com.atguigu.gmall.bean.PmsSearchSkuInfo;

import java.io.IOException;
import java.util.List;

public interface SearchService {
    List<PmsSearchSkuInfo> search(PmsSearchParam pmsSearchParam) throws IOException;
}
