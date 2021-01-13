package com.fengcloud.papercheck.controller;

import com.fengcloud.papercheck.model.CheckInfo;
import com.fengcloud.papercheck.model.RepeatInfo;
import com.fengcloud.papercheck.service.CheckService;
import com.fengcloud.papercheck.service.ReportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CheckController {
    @Resource
    private CheckService checkService;
    @Resource
    private ReportService reportService;

    /**
     * 跳转到check页面
     *
     * @return
     */
    @RequestMapping("/check")
    public String check() {
        return "check";
    }

    /**
     * 同步查重
     *
     * @return
     */
    @RequestMapping("/docheck")
    public String doCheck(HttpServletRequest request, HttpServletResponse response, MultipartFile paper_doc, Model model) {
        //保存文件
        String path = checkService.saveFile(request, paper_doc);
        //读取文件内容
        String text = checkService.getDocContent(path);
        //获取检测信息
        List<CheckInfo> infos = checkService.doCheck(text);
        //获取重复率信息
        RepeatInfo repeatInfo = reportService.getRepeatRadio(infos);
        model.addAttribute("title",paper_doc.getOriginalFilename());
        model.addAttribute("infos",infos);
        model.addAttribute("repeatInfo",repeatInfo);
        String zipPath = reportService.buildReport(request,response,model.asMap());
        return "report";
    }
    /**
     * 异步查重
     *
     * @return
     */
    @RequestMapping("/docheckajax")
    @ResponseBody
    public String docheckajax(HttpServletRequest request, HttpServletResponse response, MultipartFile paper_doc) {
        //保存文件
        String path = checkService.saveFile(request, paper_doc);
        //读取文件内容
        String text = checkService.getDocContent(path);
        //获取检测信息
        List<CheckInfo> infos = checkService.doCheck(text);
        //获取重复率信息
        RepeatInfo repeatInfo = reportService.getRepeatRadio(infos);
        //组装参数，构建检测报告
        Map<String, Object> attributeMap = new HashMap<>();
        attributeMap.put("title",paper_doc.getOriginalFilename());
        attributeMap.put("infos",infos);
        attributeMap.put("repeatInfo",repeatInfo);
        String zipPath = reportService.buildReport(request,response,attributeMap);
        return "success";
    }
}
