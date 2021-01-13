package com.fengcloud.papercheck.constant;

public class SysConst {
    /**
     * 连续多少字重复判定为抄袭,默认7个
     */
    public static int repeatSwitch = 7;
    /**
     * 文件上传地址
     */
    public static String uploadPath = "C:/data/papercheck/upload";
    /**
     * 模板文件夹地址
     */
    public static String templateDir = "C:/data/papercheck/template";
    /**
     * html模板地址
     */
    public static String htmlTemplatePath = "/全文标明引文.html";
    /**
     * 查重结果占位符 : 重复字数
     */
    public static String repeatTotalKey = "{{report_repeat_total}}";
    /**
     * 查重结果占位符 : 论文总字数
     */
    public static String paperTotalKey = "{{report_paper_total}}";
    /**
     * 查重结果占位符 : 论文总字数
     */
    public static String repeatRadioKey = "{{report_repeat_radio}}";

    /**
     * 查重结果占位符 : 论文全文
     */
    public static String clippedContentKey = "{{report_clipped_content}}";

    /**
     * 检测结果输出目录
     */
    public static String outputDir = "C:/data/papercheck/output";

    /**
     * 是否删除复制出的临时文件
     */
    public static boolean delResource = false;
}
