package com.fengcloud.papercheck;

import com.fengcloud.papercheck.plugin.ElasticSearch.PaperUpload;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

@SpringBootTest
public class DataUploadTest {

    /**
     * 处理目录
     * @throws Exception
     */
    @Test
    public void uploadFiles() throws Exception {
        PaperUpload upload = new PaperUpload();
        upload.uploadDir("D:\\Work\\paper");
        upload.closeClient();
    }

}
