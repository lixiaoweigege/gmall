package com.atguigu.gmall.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.atguigu.gmall.bean.PmsSearchParam;
import com.atguigu.gmall.bean.PmsSearchSkuInfo;
import com.atguigu.gmall.service.SearchService;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SearchServiceImpl implements SearchService {
    @Autowired
    private JestClient jestClient;


    @Override
    public List<PmsSearchSkuInfo> search(PmsSearchParam pmsSearchParam) throws IOException {
        String Dsl = getDsl(pmsSearchParam);
        Search search = new Search.Builder(Dsl).addIndex("gmallsku0615").addType("pmsSearchSkuInfo").build();
        SearchResult searchResult = jestClient.execute(search);
        List<SearchResult.Hit<PmsSearchSkuInfo, Void>> hits = searchResult.getHits(PmsSearchSkuInfo.class);
        List<PmsSearchSkuInfo> pmsSearchSkuInfos = new ArrayList<>();
        for (SearchResult.Hit<PmsSearchSkuInfo, Void> hit : hits) {
            PmsSearchSkuInfo pmsSearchSkuInfo=hit.source;

           // 处理高亮显示，如果不做是否存在hightlight的判断，则会报错

            Map<String, List<String>> highlight = hit.highlight;
            if (highlight!=null&&highlight.size()>0){
                List<String> list = highlight.get("skuName");
                String skuName = list.get(0);
                pmsSearchSkuInfo.setSkuName(skuName);
            }
            pmsSearchSkuInfos.add(pmsSearchSkuInfo);
        }

        return pmsSearchSkuInfos;
    }

    //生成查询语句的方法
    public String getDsl(PmsSearchParam pmsSearchParam) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        String catalog3Id = pmsSearchParam.getCatalog3Id();
        String keyword = pmsSearchParam.getKeyword();
        String[] valueId = pmsSearchParam.getValueId();

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        if (StringUtils.isNotBlank(catalog3Id)) {
            TermsQueryBuilder termsQueryBuilder = new TermsQueryBuilder("catalog3Id", catalog3Id);
            boolQueryBuilder.filter(termsQueryBuilder);
        }
        if (StringUtils.isNotBlank(keyword)) {
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", keyword);
            boolQueryBuilder.must(matchQueryBuilder);
            //设置高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuName");
            highlightBuilder.preTags("<span style='color:red;font-weight:bolder;'>");
            highlightBuilder.postTags("</span>");
            sourceBuilder.highlight(highlightBuilder);
        }
        if(valueId!=null&&valueId.length>0){
            for (String s : valueId) {
                TermsQueryBuilder termsQueryBuilder=new TermsQueryBuilder("skuAttrValueList.valueId",s);
                boolQueryBuilder.filter(termsQueryBuilder);
            }
        }
        sourceBuilder.query(boolQueryBuilder);
        String Dsl = sourceBuilder.query(boolQueryBuilder).toString();
        System.out.println(Dsl);
        return Dsl;
    }
}
