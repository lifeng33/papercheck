package com.fengcloud.papercheck.util;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 论文内容分割
 */
public class PaperCutter {
    /**
     * 单次查询最小长度
     */
    private static int onceCheckMinLength = 7;
    /**
     * 单次查询最大长度
     */
    private static int onceCheckMaxLength = 30;

    /**
     * 固定长度分割字符串
     * @param paperText
     * @return
     */
    public static List<String> splitFixed(String paperText){
        //删除所有标点符号
        //paperText = paperText.replaceAll( "[\\pP+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]" , "");
        int paperLength = paperText.length();
        List<String> queries = new ArrayList<String>();
        int startIndex = 0;
        while (startIndex + onceCheckMinLength < paperLength) {
            String nextQuery = paperText.substring(startIndex, startIndex + onceCheckMinLength);
            queries.add(nextQuery);
            startIndex = startIndex + onceCheckMinLength;
        }
        queries.add(paperText.substring(startIndex));
        return queries;
    }

    /**
     * 使用IK分词器分割字符串
     * @param text
     * @return
     */
    public static List<String> splitIK(String text){
        List<String> queries = new ArrayList<String>();
        StringReader sr=new StringReader(text);
        IKSegmenter ik=new IKSegmenter(sr, true);
        try {
            int beginPosition = 0;
            //下一个词
            Lexeme lex = null;
            while ((lex=ik.next())!=null) {
                int endPosition = lex.getEndPosition();
                 if(endPosition - beginPosition >= onceCheckMinLength ){
                     String query = text.substring(beginPosition,endPosition);
                     //所取字符串去除标点和空白，长度仍满7个字，则确认分割，否则继续添加
                     String word = query.replaceAll("[\\pP\\s*]", "");
                     if(word.length() >= onceCheckMinLength){
                         queries.add(query);
                         beginPosition = endPosition ;
                     }
                 }
            }
            //最后剩余不足一次检索的字符串拼接到最后一个检索字符串末尾
            if(beginPosition < text.length()){
                String lastQuery = queries.get(queries.size()-1) + text.substring(beginPosition);
                queries.set(queries.size()-1,lastQuery);

            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return queries;
    }

    /**
     * 首先按照标点符号分割，超长的进行切割
     * @param paperText
     * @return
     */
    public static List<String> splitElastic(String paperText){
        int paperLength = paperText.length();
        List<String> queries = new ArrayList<String>();
        int startIndex = 0;
        while (startIndex < paperLength) {
            //下一个标点位置
            int endIndex = getSplitEndIndex(paperText,startIndex);
            //subString 的结束位置需要在标点位置加1，加1之后大于论文长度则取论文长度
            int subEndIndex = endIndex < paperLength ? (endIndex + 1) : paperLength;
            String nextQuery = paperText.substring(startIndex, subEndIndex);
            queries.add(nextQuery);
            startIndex = subEndIndex;
        }
        return queries;
    }
    /**
     * 找到下一句话的长度
     *
     * @param startIndex
     * @return
     */
    private static int getSplitEndIndex(String paperText,int startIndex) {
        int paperLength = paperText.length();
        int endIndex = getNextSplitIndex(paperText,startIndex);
        //如果找到的下一句话太短，则再加一句
        while (endIndex + 1 < paperLength && endIndex - startIndex < onceCheckMinLength) {
            endIndex = getNextSplitIndex(paperText,endIndex + 1);
        }

        //大于最大长度，直接截取
        if (endIndex - startIndex > onceCheckMaxLength) {
            endIndex = startIndex + onceCheckMaxLength;
        }
        return endIndex;
    }

    /**
     * 找到下一个分隔符位置
     *
     * @param startIndex
     * @return
     */
    private static int getNextSplitIndex(String paperText,int startIndex) {
        int paperLength = paperText.length();
        //用于切割的标点符号数组
        char[] chars = new char[]{
                ',', '.', '?', '!', ';',':',//英文符号
                '，', '。', '？', '！', '；','：',//中文符号
                '\n'//通用符号
        };
        int[] indexes = new int[chars.length];
        for (int i = 0; i < chars.length; i++) {
            indexes[i] = paperText.indexOf(chars[i], startIndex);
        }
        //返回之中的最小值
        int min = paperLength - 1;
        for (int i = 0; i < chars.length; i++) {
            if (indexes[i] > 0 && indexes[i] < min) {
                min = indexes[i];
            }
        }
        return min;
    }

    public static void main(String[] args) {
        String text = "从21世纪初到现在 这  十多年来，科学家已经\r\n证实了不少和消化道肿瘤生长，转移，诊断和治疗等相关的生物标志物，并且新的生物标志物也陆续被文献报道。这样的工作多数是通过对比肿瘤样本和正常组织的组学数据，进行差异表达基因分析，并结合生物学实验与临床试验，证实一个或若干个和癌症非常相关的生物标志物[49]。";
        List<String> strs = splitIK(text);
        for(String str : strs){
            System.out.println(str);
        }
        String word = text.replaceAll("[\\pP\\s*]", "");
        System.out.println(word);
    }
}
