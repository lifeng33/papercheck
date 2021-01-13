package com.fengcloud.papercheck;

import com.fengcloud.papercheck.model.CheckInfo;
import com.fengcloud.papercheck.plugin.ElasticSearch.PaperSearch;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class DataSearchTest {

    @Test
    public void search() throws Exception {
        CheckInfo checkInfo = new CheckInfo();
        checkInfo.setCheck("信息承载功能，能激发学生学习兴趣");
        PaperSearch search = new PaperSearch();
        search.searchText(checkInfo);
        search.closeClient();
    }
}
