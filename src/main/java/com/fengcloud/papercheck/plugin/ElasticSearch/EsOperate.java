package com.fengcloud.papercheck.plugin.ElasticSearch;

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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EsOperate {
    /**
     * 主机地址
     */
    private String host = "localhost";
    /**
     * 端口号
     */
    private int port = 9200;
    /**
     * 模式名称
     */
    private String scheme = "http";

    /**
     * 客户端对象
     */
    private RestHighLevelClient client;

    public EsOperate(){
        //初始化得到一个RestHighLevelClient实例
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost(host, port, scheme)
                ));
    }
    /**
     * 创建索引
     * @throws IOException
     */
    public boolean creatIndex(String indexName,XContentBuilder mappingBuilder) throws IOException {

        //创建索引对象
        CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
        //设置参数
        createIndexRequest.settings(Settings.builder().put("number_of_shards", "1").put("number_of_replicas", "0"));

        createIndexRequest.mapping(mappingBuilder);
        //执行创建索引库
        CreateIndexResponse createIndexResponse = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        //得到响应
        return createIndexResponse.isAcknowledged();
    }

    /**
     * 验证索引是否存在
     * @throws IOException
     */
    public boolean indexExist(String indexName) throws IOException {
        GetIndexRequest request = new GetIndexRequest(indexName);
        request.local(false);
        request.humanReadable(true);
        return client.indices().exists(request, RequestOptions.DEFAULT);
    }
    /**
     * 删除索引
     */
    public boolean deleteIndex(String indexName) throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        request.indicesOptions(IndicesOptions.LENIENT_EXPAND_OPEN);
        AcknowledgedResponse response = client.indices().delete(request, RequestOptions.DEFAULT);
        return response.isAcknowledged();
    }

    /**
     * 创建更新文档
     */
    public IndexResponse createDocument(String indexName,String id,Map<String, Object> map) throws IOException {
        IndexRequest request = new IndexRequest(indexName).id(id).source(map);
        request.source(map);
        return client.index(request, RequestOptions.DEFAULT);
    }

    /**
     * 批量添加或更新文档
     */
    public BulkResponse bulkDocument(BulkRequest requests) throws IOException {
        return client.bulk(requests, RequestOptions.DEFAULT);
    }

    /**
     * 根据ID搜索文档
     */
    public Map<String, Object> getDocById(String indexName,String id) throws IOException {
        //查询请求对象
        GetRequest getRequest = new GetRequest(indexName, id);

        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        //得到文档的内容
        return getResponse.getSourceAsMap();
    }

    /**
     * 查询文档
     */
    public SearchResponse search(String indexName,SearchSourceBuilder builder) throws IOException {
        SearchRequest request = new SearchRequest(indexName);
        return client.search(request, RequestOptions.DEFAULT);
    }

    /**
     * 删除索引中的全部数据
     * @throws IOException
     */
    public BulkByScrollResponse removeAll(String indexName) throws IOException {
        DeleteByQueryRequest request = new DeleteByQueryRequest(indexName);
        request.setQuery(new MatchAllQueryBuilder());
        return client.deleteByQuery(request, RequestOptions.DEFAULT);
    }

    /**
     * 关闭客户端
     * @throws IOException
     */
    public void close() throws IOException {
        client.close();
    }
}
