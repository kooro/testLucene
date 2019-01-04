package com.weigram.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;

public class LuceneManager {
    public IndexWriter getIndexWriter() throws Exception {
        Directory directory = FSDirectory.open(new File("F:\\temp\\index"));
//        指定一个分析器，对文档内容进行分析
        Analyzer analyzer = new IKAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
//        创建一个indexwriter对象
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);
        return indexWriter;
    }

    public IndexSearcher getIndexSearcher() throws Exception {
        //        创建一个DIrectory对象，也就是索引库存放的位置
        Directory directory = FSDirectory.open(new File("F:\\temp\\index"));
//        创建一个indexReader对象，需要指定Directory对象
        IndexReader indexReader = DirectoryReader.open(directory);
//        创建一个indexsearcher对象，需要指定indexReader对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        return indexSearcher;
    }

    //全删除
    @Test
    public void testAllDelete() throws Exception {
        IndexWriter indexWriter = getIndexWriter();
        indexWriter.deleteAll();
        indexWriter.close();
    }

    @Test
    public void testDelete() throws Exception {
        IndexWriter indexWriter = getIndexWriter();
        Query query = new TermQuery(new Term("fileName", "java"));
        indexWriter.deleteDocuments(query);
        indexWriter.close();
    }

    @Test
    public void testUpdate() throws Exception {
        IndexWriter indexWriter = getIndexWriter();
        Document doc = new Document();
        doc.add(new TextField("fileName", "test", Field.Store.YES));
        doc.add(new TextField("fileContent", "java is great!", Field.Store.YES));
        indexWriter.addDocument(doc);
        indexWriter.close();
    }

    public void printResult(IndexSearcher indexSearcher, Query query) throws Exception {
        TopDocs topDocs = indexSearcher.search(query, 10);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
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
    }

    @Test
    public void testSearch() throws Exception {
        IndexSearcher indexSearcher = getIndexSearcher();
        Query query = new TermQuery(new Term("fileContent", "java"));
        printResult(indexSearcher, query);
//        关闭IndexReader对象
        indexSearcher.getIndexReader().close();
    }

    @Test
    public void mathcAllDocsQueryTest() throws Exception {
        IndexSearcher indexSearcher = getIndexSearcher();
        Query query = new MatchAllDocsQuery();
        System.out.println(query);
        printResult(indexSearcher, query);
    }

    @Test
    public void numericRangeQueryTest() throws Exception {
        IndexSearcher indexSearcher = getIndexSearcher();
        Query query = NumericRangeQuery.newLongRange("fileSize", 10L, 600L, true, true);
        printResult(indexSearcher, query);
    }

    @Test
    public void booleanQueryTest() throws Exception {
        IndexSearcher indexSearcher = getIndexSearcher();
        BooleanQuery booleanQuery = new BooleanQuery();
        Query query1 = new TermQuery(new Term("fileName", "java"));
        Query query2 = new TermQuery(new Term("fileContent", "java"));
        booleanQuery.add(query1, BooleanClause.Occur.SHOULD);
        booleanQuery.add(query2, BooleanClause.Occur.MUST);
        System.out.println(booleanQuery);
        printResult(indexSearcher, booleanQuery);
        indexSearcher.getIndexReader().close();
    }

    @Test
    public void queryParserTest() throws Exception {
        IndexSearcher indexSearcher = getIndexSearcher();
        QueryParser queryParser = new QueryParser("fileContent", new IKAnalyzer());
        Query query1 = queryParser.parse("fileName:java");
        Query query2 = queryParser.parse("+fileName:java +fileContent:java");
        System.out.println(query2);
        BooleanQuery booleanQuery = new BooleanQuery();
        booleanQuery.add(query1, BooleanClause.Occur.MUST);
        booleanQuery.add(query2, BooleanClause.Occur.MUST);
        System.out.println(booleanQuery);
        printResult(indexSearcher, booleanQuery);
        indexSearcher.getIndexReader().close();
    }

    @Test
    public void multiFieldQueryParserTest() throws Exception {
        IndexSearcher indexSearcher = getIndexSearcher();
        String[] fields = {"fileName", "fileContent"};
        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(fields, new IKAnalyzer());
        Query query = queryParser.parse("java is great");
        System.out.println(query);
        printResult(indexSearcher, query);
        indexSearcher.getIndexReader().close();
    }
}
