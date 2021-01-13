package com.fengcloud.papercheck.plugin.ElasticSearch;

import org.apache.http.HttpHost;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class PaperUpload {
    /**
     * 创建请求客户端
     */
    private RestHighLevelClient client = null;
    /**
     * 保存待上传的请求
     */
    private BulkRequest requests = null;
    /**
     * 索引名
     */
    private String index = "paper";
    /**
     * BulkRequest批量操作大小
     */
    private int bulkSize = 50;
    /**
     * 文件最大20M
     */
    private int maxFileSize = 20971520;
    /**
     * 计数上传了多少篇
     */
    private int paperCount = 0;
    /**
     * 上次成功上传的位置，同一文件夹首次上传值为0
     */
    private int lastSuccessPosition = 0;

    /**
     * 构造方法，初始化client 和 requests
     */
    public PaperUpload(){
        client = new RestHighLevelClient(
                RestClient.builder( new HttpHost("localhost", 9200, "http"))
        );
        requests = new BulkRequest();
    }
    /**
     * 处理目录
     */
    public void uploadDir(String path) throws IOException {
        File file = new File(path);
        scanDir(file);
        flushBulkRequest();
    }
    /**
     * 递归加载目录下的所有文件
     * @param file
     */
    private void scanDir(File file){
        File[] files = file.listFiles();
        for (File fileIndex : files) {
            //如果这个文件是目录，则进行递归搜索
            if (fileIndex.isDirectory()) {
                scanDir(fileIndex);
            }else{
                bulkRequest(fileIndex);
            }
        }
    }

    /**
     * 添加到批量处理请求
     * @param file
     */
    private void bulkRequest(File file){
        String fileName = file.getName();
        if(!fileName.toLowerCase().endsWith(".doc") && !fileName.toLowerCase().endsWith(".docx")&& !fileName.toLowerCase().endsWith(".pdf")){
            System.out.println("无效文件类型忽略："+fileName);
            return;
        }
        //大于20M的文件忽略
        if(file.length() > maxFileSize){
            System.out.println("文件太大忽略："+file.getPath());
            return;
        }
        //计数器+1
        ++paperCount;
        //之前已经添加成功的文件跳过
        if(paperCount < lastSuccessPosition){
            System.out.println(paperCount + "、已上传跳过："+file.getPath());
            return;
        }
        System.out.println(paperCount + "、开始读取："+file.getPath());
        String title =fileName.substring(0,fileName.lastIndexOf("."));
        String content = readFile(file);
        if(StringUtils.hasText(title) && StringUtils.hasText(content)){
            Map<String, Object> paraMap = new HashMap<>();
            paraMap.put("title", title);
            /*
            int summeryStartPos = content.indexOf("摘要");
            if(summeryStartPos > 0){
                paraMap.put("head", content.substring(0,summeryStartPos));
                int keyStartPos = content.indexOf("关键词");
                if(keyStartPos > 0 && keyStartPos > summeryStartPos){
                    paraMap.put("summery", content.substring(summeryStartPos,keyStartPos));
                }
            }
            */
            paraMap.put("content", content);
            //文件名拼接文件长度作为ID
            String id = title+content.length();
            IndexRequest request = new IndexRequest(index).id(id).source(paraMap);
            //requests.add(request);
        }
        if(requests.numberOfActions() >= bulkSize){
            flushBulkRequest();
        }
    }

    /**
     * 清空未发起的请求
     */
    private void flushBulkRequest() {
        if(requests.numberOfActions() > 0){
            try {
                BulkResponse responses = client.bulk(requests, RequestOptions.DEFAULT);
                //清空
                requests = new BulkRequest();
                System.out.println( responses.status().getStatus());
                System.out.println(responses.status().name());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 读取文件
     * @param file
     */
    private String readFile(File file) {
        String fileName = file.getName();
        if(fileName.toLowerCase().endsWith("pdf")){
            return readPDF(file);
        }
        if(fileName.toLowerCase().endsWith("doc")){
            return readDoc(file);
        }
        if(fileName.toLowerCase().endsWith("docx")){
            return readDocx(file);
        }
        return null;
    }

    /**
     * 读取PDF文件
     * @param file
     */
    public String readPDF(File file){
        try {
            PDDocument document = PDDocument.load(file);
            PDFTextStripper textStripper = new PDFTextStripper();
            String text = textStripper.getText(document);
            document.close();
            return text;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取Word文件
     * @param file
     */
    private String readDoc(File file) {
        try {
            InputStream is = new FileInputStream(file);
            HWPFDocument doc = new HWPFDocument(is);
            String text = doc.getDocumentText();
            is.close();
            doc.close();
            return text;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 读取docx文档
     * @param file
     * @return
     */
    private String readDocx(File file) {
        try {
            InputStream is = new FileInputStream(file);
            XWPFDocument doc = new XWPFDocument(is);
            XWPFWordExtractor xwpfWordExtractor = new XWPFWordExtractor(doc);
            String text = xwpfWordExtractor.getText();
            xwpfWordExtractor.close();
            doc.close();
            is.close();
            return text;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
