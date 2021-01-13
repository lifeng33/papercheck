package com.fengcloud.papercheck;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;

@SpringBootTest
public class IKTest {
    @Test
    public void testIK() throws IOException {
        String text="基于微课的初中数学智慧课堂，包括智慧课堂的教学流程、教学形式以及教学目标。";
        StringReader sr=new StringReader(text);
        IKSegmenter ik=new IKSegmenter(sr, true);
        Lexeme lex=null;
        while((lex=ik.next())!=null) {
            System.out.print(lex.getBegin()+" ");
            System.out.print(lex.getBeginPosition()+" ");
            System.out.print(lex.getEndPosition()+" ");
            System.out.print(lex.getLength()+" ");
            System.out.print(lex.getOffset()+" ");
            System.out.print(lex.getLexemeText() + "|");
        }
    }
}
