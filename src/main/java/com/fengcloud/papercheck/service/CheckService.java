package com.fengcloud.papercheck.service;

import com.fengcloud.papercheck.constant.SysConst;
import com.fengcloud.papercheck.model.CheckInfo;
import com.fengcloud.papercheck.plugin.ElasticSearch.PaperSearch;
import com.fengcloud.papercheck.util.PaperCutter;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CheckService {
    /**
     * 将文件保存在磁盘
     *
     * @param request
     * @param doc
     */
    public String saveFile(HttpServletRequest request, MultipartFile doc) {
        String requestSessionId = request.getRequestedSessionId();
        try {
            //子目录（年月日分层）
            SimpleDateFormat sdf = new SimpleDateFormat("/yyyy/MM/dd/");
            String offsetPath = sdf.format(new Date());
            //文件夹路径
            String fullDir = SysConst.uploadPath + offsetPath;
            File fileDir = new File(fullDir);
            //如果不存在，则创建文件夹
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }
            //文件路径
            String fullPath = fullDir + requestSessionId + "-" + doc.getOriginalFilename();
            File file = new File(fullPath);
            doc.transferTo(file);
            return fullPath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 从文件中解析待降重内容
     *
     * @param path
     * @return
     */
    public String getDocContent(String path) {
        try {
            InputStream inputStream = new FileInputStream(path);
            if (path.toLowerCase().endsWith(".doc")) { // doc为后缀的
                WordExtractor doc = new WordExtractor(inputStream);
                return doc.getText().trim();
            }
            if (path.toLowerCase().endsWith(".docx")) { // docx为后缀的
                XWPFWordExtractor docx = new XWPFWordExtractor(new XWPFDocument(inputStream));
                return docx.getText().trim();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 执行检测
     *
     * @return
     */
    public List<CheckInfo> doCheck(String text) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String startTime = sdf.format(new Date());
        System.out.println("开始时间" + startTime);//拆分字符串
        List<String> queries = PaperCutter.splitIK(text);
        int length = queries.size();
        //构造检测对象
        List<CheckInfo> infos = new ArrayList<CheckInfo>();
        PaperSearch paperSearch = new PaperSearch();
        for(int i=0;i<length;i++){
            CheckInfo info = new CheckInfo();
            info.setTotal(length);
            info.setIndex(i+1);
            info.setCheck(queries.get(i));
            infos.add(info);
            paperSearch.searchText(info);
        }
        paperSearch.closeClient();
        String endTime = sdf.format(new Date());
        System.out.println("开始时间" + startTime + " , 结束时间" + endTime);
        return infos;
    }
}
