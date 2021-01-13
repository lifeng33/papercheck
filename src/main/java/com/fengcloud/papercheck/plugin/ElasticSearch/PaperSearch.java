package com.fengcloud.papercheck.plugin.ElasticSearch;

import com.fengcloud.papercheck.model.CheckInfo;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.io.IOException;

public class PaperSearch {
    /**
     * 创建请求客户端
     */
    private RestHighLevelClient client = null;

    /**
     * 索引名
     */
    private String index = "paper";
    /**
     * 构造方法，初始化client 和 requests
     */
    public PaperSearch(){
        client = new RestHighLevelClient(
                RestClient.builder( new HttpHost("localhost", 9200, "http"))
        );
    }

    /**
     * 搜索
     * @param checkInfo
     */
    public void searchText(CheckInfo checkInfo){
        SearchRequest request = new SearchRequest(index);
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //精确匹配查询的短语，需要全部单词和顺序要完全一样，标点符号除外，允许对最后一个词条前缀匹配。
        builder.query(QueryBuilders.matchPhrasePrefixQuery("content", checkInfo.getCheck()));
        //指定返回字段
        builder.fetchSource("title",null);
        builder.from(0).size(1); // 分页
        request.source(builder);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            long hitCount = response.getHits().getTotalHits().value;
            if(hitCount > 0){
                String title = response.getHits().getAt(0).getSourceAsMap().get("title").toString();
                checkInfo.setTitle(title);
                checkInfo.setRepeat(true);
            }else{
                checkInfo.setRepeat(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭客户端
     */
    public void closeClient(){
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
