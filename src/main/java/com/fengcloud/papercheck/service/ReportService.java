package com.fengcloud.papercheck.service;

import com.fengcloud.papercheck.model.CheckInfo;
import com.fengcloud.papercheck.constant.SysConst;
import com.fengcloud.papercheck.model.RepeatInfo;
import com.fengcloud.papercheck.util.CommonUtil;
import com.fengcloud.papercheck.util.FileUtil;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {
    /**
     * thymeleaf模板引擎
     */
    @Resource
    private TemplateEngine templateEngine;

    /**
     * 生成报告
     * @param request
     * @param response
     * @param attributeMap
     * @return
     */
    public String buildReport(HttpServletRequest request, HttpServletResponse response, Map<String, Object> attributeMap) {
        try {
            //将模板文件拷贝到输出目录
            String outputDir = copyTemplateFile();
            //构建result.html
            String html = buildHtml(request,response,attributeMap);
            //输出html到输出目录
            String htmlPath = outputDir + SysConst.htmlTemplatePath;
            FileUtil.writeHtml(htmlPath, html);
            //生成压缩文件
            String finalZipPath = outputDir + ".zip";
            FileOutputStream outputStream = new FileOutputStream(new File(finalZipPath));
            FileUtil.toZip(outputDir, outputStream, true);
            outputStream.close();
            //删除复制出的文件
            if (SysConst.delResource) {
                FileUtil.delFile(new File(outputDir));
            }
            return finalZipPath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取重复率
     * @param infos
     * @return
     */
    public RepeatInfo getRepeatRadio(List<CheckInfo> infos){
        int totalWords = 0;
        int repeatWords = 0;
        for (CheckInfo info : infos) {
            String query = info.getCheck();
            totalWords += query.length();
            //本段抄袭
            if (info.getRepeat()) {
                repeatWords += query.length();
            }
        }
        RepeatInfo repeatInfo = new RepeatInfo();
        repeatInfo.setTotal(totalWords);
        repeatInfo.setRepeat(repeatWords);
        repeatInfo.setRepeatRadio((repeatWords * 100.0) / totalWords);
        return repeatInfo;
    }

    /**
     * 复制模板文件到输出目录
     * @return 输出目录
     * @throws IOException
     */
    private String copyTemplateFile() throws IOException {
        //复制模板文件夹到存放检测结果文件夹，目录（年月日分层）
        SimpleDateFormat sdf = new SimpleDateFormat("/yyyy/MM/dd/");
        String offsetPath = sdf.format(new Date());
        //临时文件夹路径
        String random = CommonUtil.getId();
        String outputDir = SysConst.outputDir + offsetPath + random;
        FileUtil.copyDir(SysConst.templateDir, outputDir);
        return outputDir;
    }

    /**
     * 生成html
     * @param request
     * @param response
     * @param attributeMap
     */
    private String buildHtml(HttpServletRequest request, HttpServletResponse response, Map<String, Object> attributeMap) {
        WebContext context = new WebContext(request,response,request.getServletContext());
        context.setVariables(attributeMap);
        return templateEngine.process("report",context);
    }
}
