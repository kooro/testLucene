package com.weigram.lucene;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

public class FirstLucene {
    @Test
    public void test1() throws IOException {
//        指定索引库存放位置Directory对象
        Directory directory = FSDirectory.open(new File("F:\\temp\\index"));
//        指定一个分析器，对文档内容进行分析
//        StandardAnalyzer analyzer = new StandardAnalyzer();
        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
//        创建一个indexwriter对象
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);

        File docFile = new File("F:\\temp\\document");
        File[] files = docFile.listFiles();
        for (File f : files) {
            String fileName = f.getName();
//        创建field对象，将field添加到document对象中
            Field fileNameField = new TextField("fileName", fileName, Field.Store.YES);
            long fileSize = FileUtils.sizeOf(f);
            Field fileSizeField = new LongField("fileSize", fileSize, Field.Store.YES);
            String filePath = f.getPath();
            Field filePathField = new StoredField("filePath", filePath);
            String fileContent = FileUtils.readFileToString(f);
            Field fileContentField = new TextField("fileContent", fileContent, Field.Store.NO);
//        创建document对象
            Document document = new Document();
            document.add(fileNameField);
            document.add(fileSizeField);
            document.add(filePathField);
            document.add(fileContentField);

//        使用indexwriter对象将document对象写入索引库，此过程中进行索引创建，并将索引和document对象写入索引库
            indexWriter.addDocument(document);
        }
//        关闭indexwriter对象
        indexWriter.close();
    }

    @Test
    public void  test2() throws IOException {
//        创建一个DIrectory对象，也就是索引库存放的位置
        Directory directory = FSDirectory.open(new File("F:\\temp\\index"));
//        创建一个indexReader对象，需要指定Directory对象
        IndexReader indexReader = DirectoryReader.open(directory);
//        创建一个indexsearcher对象，需要指定indexReader对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
//        创建一个TermQuery对象，指定查询的域和查询的关键词
        Query query = new TermQuery(new Term("fileContent","Maven"));
//        执行查询
        TopDocs topDocs = indexSearcher.search(query,10);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
//        返回查询结果，便利查询结果并输出
        for (ScoreDoc scoreDoc : scoreDocs) {
            int doc = scoreDoc.doc;
            Document document = indexSearcher.doc(doc);
            // 文件名称
            String fileName = document.get("fileName");
            System.out.println(fileName);
            // 文件内容
            String fileContent = document.get("fileContent");
            System.out.println(fileContent);
            // 文件大小
            String fileSize = document.get("fileSize");
            System.out.println(fileSize);
            // 文件路径
            String filePath = document.get("filePath");
            System.out.println(filePath);
            System.out.println("------------");
        }
//        关闭IndexReader对象
        indexReader.close();
    }
}
