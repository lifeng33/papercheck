package com.fengcloud.papercheck;

import com.fengcloud.papercheck.plugin.ElasticSearch.EsOperate;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class EsOperateTest {

    @Test
    public void contextLoads() {
    }

    /**
     * 创建索引
     * @throws IOException
     */
    @Test
    public void testCreatIndex() throws IOException {
        EsOperate opt = new EsOperate();
        //指定映射
        XContentBuilder mappingBuilder = JsonXContent.contentBuilder()
            .startObject()
                .startObject("properties")
                    .startObject("title")
                        .field("type", "text")
                        .field("analyzer", "ik_max_word")
                        .field("index", "true")
                    .endObject()
                    .startObject("content")
                        .field("type", "text")
                        .field("analyzer", "ik_max_word") // ik_max_word 这个分词器是ik的，可以去github上搜索安装es的ik分词器插件
                        .field("index", "true")
                    .endObject()
                .endObject()
            .endObject();
        boolean result = opt.creatIndex("test",mappingBuilder);
        System.out.println(result);
        opt.close();
    }

    /**
     * 验证索引是否存在
     * @throws IOException
     */
    @Test
    public void testExistIndex() throws IOException {
        EsOperate opt = new EsOperate();
        boolean result = opt.indexExist("test");
        System.out.println(result);
        opt.close();
    }
    /**
     * 删除索引
     */
    @Test
    public void testDeleteIndex() throws IOException {
        EsOperate opt = new EsOperate();
        boolean result = opt.deleteIndex("test");
        System.out.println(result);
        opt.close();
    }

    /**
     * 创建更新文档
     */
    @Test public void testCreateDocument() throws IOException {
        EsOperate opt = new EsOperate();
        Map<String, Object> map = new HashMap<>();
        map.put("title", "上海自来水来自海上");
        map.put("content", "上海自来水来自海上上海自来水来自海上上海自来水来自海上");
        IndexResponse response = opt.createDocument("test","1",map);
        // not exist: result: code: 201, status: CREATED
        // exist: result: code: 200, status: OK
        System.out.println( response.status().getStatus());
        System.out.println(response.status().name());
        opt.close();
    }

    /**
     * 批量添加或更新文档
     */
    @Test
    public void bulkDocument() throws IOException {

        EsOperate opt = new EsOperate();
        BulkRequest requests = new BulkRequest();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("title", "我是中国人");
        map1.put("content", "我是中国人我是中国人我是中国人");
        IndexRequest request1 = new IndexRequest("test").id("1").source(map1);
        requests.add(request1);
        Map<String, Object> map2 = new HashMap<>();
        map2.put("title", "武汉市长江大桥欢迎您");
        map2.put("content", "我是中国人武汉市长江大桥欢迎您武汉市长江大桥欢迎您武汉市长江大桥欢迎您");
        IndexRequest request2 = new IndexRequest("test").id("2").source(map2);
        requests.add(request2);
        Map<String, Object> map3 = new HashMap<>();
        map3.put("title", "上海自来水来自海上");
        map3.put("content", "我是中国人上海自来水来自海上上海自来水来自海上上海自来水来自海上");
        IndexRequest request3 = new IndexRequest("test").id("3").source(map3);
        requests.add(request3);
        BulkResponse responses = opt.bulkDocument(requests);
        // not exist: result: code: 200, status: OK
        // exist: result: code: 200, status: OK
        System.out.println( responses.status().getStatus());
        System.out.println(responses.status().name());
        opt.close();
    }

    /**
     * 根据ID搜索文档
     */
    @Test
    public void testGetDocById() throws IOException {
        EsOperate opt = new EsOperate();
        Map<String, Object> sourceAsMap =  opt.getDocById("test","1");
        System.out.println(sourceAsMap);
        opt.close();
    }

    /**
     * 查询文档
     */
    @Test
    public void searchDocument() throws IOException {
        EsOperate opt = new EsOperate();
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //精确匹配查询的短语，需要全部单词和顺序要完全一样，标点符号除外，允许对最后一个词条前缀匹配。
        builder.query(QueryBuilders.matchPhrasePrefixQuery("content", "国人上海自来水来自海"));
        //会将搜索词分词，再与目标查询字段进行匹配，若分词中的任意一个词与目标字段匹配上，则可查询到。
        //builder.query(QueryBuilders.matchQuery("content", "我是一个中国人上海的自来水来自海上上海自来水来自海上上海自来水来自海上"));
        builder.from(0).size(10); // 分页
        SearchResponse response = opt.search("test",builder);
        System.out.println("Response: " + response.toString());
        System.out.println("TotalShards: " + response.getTotalShards());
        for (SearchHit documentFields : response.getHits()) {
            System.out.println("result："+ documentFields.toString());
            System.out.println("code："+ response.status().getStatus());
            System.out.println("status："+ response.status().name());
        }
        opt.close();
    }

    /**
     * 删除索引中的全部数据
     * @throws IOException
     */
    @Test
    public void removeAll() throws IOException {
        EsOperate opt = new EsOperate();
        BulkByScrollResponse response = opt.removeAll("test");
        System.out.println(response.toString());
        opt.close();
    }
}
