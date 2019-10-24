package com.atguigu.gmall.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.atguigu.gmall.bean.MovieBean;
import com.atguigu.gmall.bean.PmsSearchParam;
import com.atguigu.gmall.bean.PmsSearchSkuInfo;
import com.atguigu.gmall.bean.PmsSkuInfo;
import com.atguigu.gmall.search.service.impl.SearchServiceImpl;
import com.atguigu.gmall.service.SkuService;
import io.searchbox.client.JestClient;
import io.searchbox.core.*;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallSearchServiceApplicationTests {
    @Autowired
    private JestClient jestClient;
    @Reference
    SkuService skuService;


    @Test
    public void contextLoads() {
    }

    @Test
    public void testEs() throws IOException {
        MovieBean movieBean = new MovieBean("2", "周芷若", "九阴白骨爪");
        SearchSourceBuilder s = new SearchSourceBuilder();
        // 通过http的rest风格的请求发送dsl的json语句
        //增加
        Index index = new Index.Builder(movieBean).index("movie").type("movie").id("2").build();
        //删除
        Delete delete = new Delete.Builder(null).build();
        //修改
        Update update = new Update.Builder(null).index("").type("").id("").build();
        //查询
        Search search = new Search.Builder("{}").addIndex("movie").addType("movie").build();
        SearchResult searchResult = jestClient.execute(search);
        List<SearchResult.Hit<MovieBean, Void>> hits = searchResult.getHits(MovieBean.class);
        for (SearchResult.Hit<MovieBean, Void> hit : hits) {
            System.out.println(hit.source.toString());
        }
    }

    @Test
    public void testEsSku() throws IOException {
        List<PmsSkuInfo> pmsSkuInfos = skuService.getALLsku();
        // 转化成search的sku对象
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();

        for (PmsSkuInfo pmsSkuInfo : pmsSkuInfos) {
            PmsSearchSkuInfo pmsSearchSkuInfo = new PmsSearchSkuInfo();
            BeanUtils.copyProperties(pmsSkuInfo, pmsSearchSkuInfo);
            // 主键配型
            pmsSearchSkuInfo.setId(Long.parseLong(pmsSkuInfo.getId()));
            pmsSearchSkuInfos.add(pmsSearchSkuInfo);
        }

        for (PmsSearchSkuInfo pmsSkuInfo : pmsSearchSkuInfos) {
            Index index = new Index.Builder(pmsSkuInfo).index("gmallsku0615").type("pmsSearchSkuInfo").id(pmsSkuInfo.getId() + "").build();
            jestClient.execute(index);
        }
    }


}
