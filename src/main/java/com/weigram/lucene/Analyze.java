package com.weigram.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;

public class Analyze {
    @Test
    //所有中文都拆分成单个文字
    public void test1() throws IOException {
        Analyzer analyzer = new StandardAnalyzer();
        TokenStream tokenStream = analyzer.tokenStream("test","中国人学习英语第一句话是：hello world！");
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()){
            System.out.println("start -> "+offsetAttribute.startOffset());
            System.out.println(charTermAttribute);
            System.out.println("end -> "+offsetAttribute.endOffset());
        }
    }
    @Test
    //中文会拆分成不同的词
    public void test2() throws IOException {
        Analyzer analyzer = new IKAnalyzer();
        TokenStream tokenStream = analyzer.tokenStream("test","中国人学习英语第一句话是：hello world！高富帅也是词");
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        tokenStream.reset();
        while (tokenStream.incrementToken()){
            System.out.println("start -> "+offsetAttribute.startOffset());
            System.out.println(charTermAttribute);
            System.out.println("end -> "+offsetAttribute.endOffset());
        }
    }
}
