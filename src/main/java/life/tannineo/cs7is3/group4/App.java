package life.tannineo.cs7is3.group4;

import life.tannineo.cs7is3.group4.entity.DocumentQuery;
import life.tannineo.cs7is3.group4.parser.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class App {

    static String INDEX_PATH = "generated_index";
    static int HITS_PER_PAGE = 1000;

    private static String[] corporaName = {"fbis", "fr94", "ft", "latimes"};

    public static void main(String args[]) throws Exception {
    	long start = System.currentTimeMillis();
        // region 0. prepare and test field
        BM25Similarity bm25Similarity = new BM25Similarity();
//        CharArraySet customStopWordSet = new CharArraySet(127, true);
//        customStopWordSet.addAll(Arrays.asList(CustomWordSet.customStopWords));
//        CharArraySet cusromNotToStemSet = new CharArraySet(100, true);
//        cusromNotToStemSet.addAll(Arrays.asList(CustomWordSet.customWordsNotToStem));
//        EnglishAnalyzer englishAnalyzer = new EnglishAnalyzer(customStopWordSet, cusromNotToStemSet);

//          MyAnalyzer myAnalyzer = new MyAnalyzer(CustomWordSet.extraLongStopwordList, CustomWordSet.customWordsNotToStem);
//          MySynonymAnalyzer mySynonymAnalyzer = new MySynonymAnalyzer(CustomWordSet.extraLongStopwordList, CustomWordSet.customWordsNotToStem);          
          Analyzer mCustomAnalyzer_Syn_stp = new CustomAnalyzer_Syn_stp(); /* use mCustomAnalyzer_Syn_stp */
        // endregion

        // region 1 + 2. corpora parsing & indexing

        //set flag to use old indexes and to save time, toIndex = flag
        boolean toIndex = true;
        if (toIndex) {

//            IndexWriterConfig iwconfig = new IndexWriterConfig(myAnalyzer);
            IndexWriterConfig iwconfig = new IndexWriterConfig(mCustomAnalyzer_Syn_stp); /* use mCustomAnalyzer_Syn_stp */
            
            iwconfig.setSimilarity(bm25Similarity);
            Directory indexDir = FSDirectory.open(Paths.get(App.INDEX_PATH));
            iwconfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE); // overwrite multiple times ???
            IndexWriter indexWriter = new IndexWriter(indexDir, iwconfig);

//        FbisParser fbisParser = new FbisParser();
//        fbisParser.readFile("../corpora/fbis/fb396001.sgm");
//        Fr94Parser fr94Parser = new Fr94Parser();
//        fr94Parser.readFile("../corpora/fr94/fr940105_1.sgm");
//        FtParser ftParser = new FtParser();
//        ftParser.readFile("../corpora/ft/ft911_1.sgm");
//        LatimesParser latimesParser = new LatimesParser();
//        latimesParser.readFile("../corpora/latimes/la010189.sgm");

            ArrayList<Document> tempDocArr = new ArrayList<>();

            for (int i = 0; i < corporaName.length; i++) {
                String corp = corporaName[i];
                File folder = new File("../corpora/" + corp);

                DocumentParser parser;
                if (i == 0) {
                    parser = new FbisParser();
                } else if (i == 1) {
                    parser = new Fr94Parser();
                } else if (i == 2) {
                    parser = new FtParser();
                } else {
                    parser = new LatimesParser();
                }

                System.out.println("Parsing " + corp + "...");

                for (File sgm : Objects.requireNonNull(folder.listFiles())) {
                    if (sgm.isFile() && sgm.getAbsolutePath().endsWith(".sgm")) {
                        tempDocArr.addAll(parser.toLucDoc(parser.readFile(sgm.getAbsolutePath())));
                    }
                }

                indexWriter.addDocuments(tempDocArr);
                System.out.println("Finished parsing " + corp + ". Docs: " + tempDocArr.size());
                tempDocArr.clear();
            }
            try {
                indexWriter.close();
            } catch (IOException e) {
                System.out.println("ERROR: an error occurred when closing the index from the directory!");
                System.out.println(String.format("ERROR MESSAGE: %s", e.getMessage()));
            }

            // endregion
        }
            // region 3. query parsing
            LinkedHashMap<String, Query> queryLinkedHashMap = new LinkedHashMap<>();
        	parse_query(mCustomAnalyzer_Syn_stp, queryLinkedHashMap);
            // endregion
        	// region 4. query searching
            search_query(bm25Similarity,queryLinkedHashMap);

            long end = System.currentTimeMillis();

            NumberFormat secFormatter = new DecimalFormat("#0.00000");
            System.out.println("Program Running time is " + secFormatter.format((end - start) / 1000d) + " seconds");

        
    }
    
    private static void parse_query(Analyzer mCustomAnalyzer_Syn_stp, LinkedHashMap<String, Query> queryLinkedHashMap) throws ParseException {
        ArrayList<DocumentQuery> documentQueries = new QueryParser().readQueries("topics");
//    for (DocumentQuery documentQuery : documentQueries) {
//        System.out.println(documentQuery.title);
//        System.out.println(documentQuery.description);
//        System.out.println(documentQuery.narrative);
//    }

        // parameter tuning
        HashMap<String, Float> boosts = new HashMap<>();

        // search_result_1586350867320 MAP = 0.2993
//    boosts.put(FieldName.TEXT.getName(), 20f);
//    boosts.put(FieldName.META.getName(), 5f);
//    boosts.put(FieldName.GRAPHIC.getName(), 3f);
//    boosts.put(FieldName.HEADLINE.getName(), 2f);
//    boosts.put(FieldName.HEADER.getName(), 2f);

        // search_result_1586351017203 MAP = 0.2870
//    boosts.put(FieldName.TEXT.getName(), 20f);
//    boosts.put(FieldName.META.getName(), 5f);
//    boosts.put(FieldName.GRAPHIC.getName(), 10f);
//    boosts.put(FieldName.HEADLINE.getName(), 5f);
//    boosts.put(FieldName.HEADER.getName(), 5f);
//    boosts.put(FieldName.HT.getName(), 5f);
//    boosts.put(FieldName.TYPE.getName(), 5f);
//    boosts.put(FieldName.SUBJECT.getName(), 5f);
//    boosts.put(FieldName.DATE.getName(), 5f);

        // search_result_1586351149955 MAP = 0.2884
//    boosts.put(FieldName.TEXT.getName(), 20f);
//    boosts.put(FieldName.META.getName(), 5f);
//    boosts.put(FieldName.GRAPHIC.getName(), 10f);
//    boosts.put(FieldName.TYPE.getName(), 5f);
//    boosts.put(FieldName.SUBJECT.getName(), 5f);
//    boosts.put(FieldName.DATE.getName(), 5f);

        // search_result_1586351229036 MAP = 0.2883
//    boosts.put(FieldName.TEXT.getName(), 20f);
//    boosts.put(FieldName.META.getName(), 10f);
//    boosts.put(FieldName.GRAPHIC.getName(), 5f);

        // search_result_1586351305185 MAP = 0.3010
//    boosts.put(FieldName.TEXT.getName(), 30f);
//    boosts.put(FieldName.META.getName(), 5f);
//    boosts.put(FieldName.GRAPHIC.getName(), 3f);
//    boosts.put(FieldName.HEADLINE.getName(), 2f);
//    boosts.put(FieldName.HEADER.getName(), 2f);

        // removed k-stem and snowball using these MAP = 0.3010
        boosts.put(FieldName.TEXT.getName(), 40f);
        boosts.put(FieldName.GRAPHIC.getName(), 3f);
        boosts.put(FieldName.HEADLINE.getName(), 2f);
        boosts.put(FieldName.HEADER.getName(), 2f);
        boosts.put(FieldName.HT.getName(), 5f);


//        MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(FieldName.getAllNamesExceptNonSense(), mySynonymAnalyzer, boosts);
        MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(FieldName.getAllNamesExceptNonSense(), mCustomAnalyzer_Syn_stp, boosts); /* use mCustomAnalyzer_Syn_stp */
        // multiFieldQueryParser.setAllowLeadingWildcard(true);
        for (DocumentQuery dq : documentQueries) {
            System.out.println("Parsing Query ID:" + dq.queryId);
//        String parsedQueryStr = dq.title + " " + dq.description;
            String parsedQueryStr = dq.title + " " + dq.description + " " + dq.narrative;
//        String parsedQueryStr = MyQueryStringParser.parseQueryString(dq.title + " " + dq.description + " " + dq.narrative);
            Query qry = multiFieldQueryParser.parse(parsedQueryStr);
            System.out.println(qry.toString());
            queryLinkedHashMap.put(dq.queryId, qry);
        }

        System.out.println("Parsed all queries... " + queryLinkedHashMap.entrySet().size() + " in total!");
        // endregion

    }
    private static void search_query(BM25Similarity bm25Similarity, LinkedHashMap<String, Query> queryLinkedHashMap) throws IOException {
        Directory dirr = FSDirectory.open(Paths.get(App.INDEX_PATH));
        DirectoryReader dirReader = DirectoryReader.open(dirr);
        IndexSearcher searcher = new IndexSearcher(dirReader);
        searcher.setSimilarity(bm25Similarity);

        ArrayList<String> resultArr = new ArrayList<>();

        for (Map.Entry<String, Query> queryEntry : queryLinkedHashMap.entrySet()) {
            TopDocs topDocs = searcher.search(queryEntry.getValue(), HITS_PER_PAGE);
            ScoreDoc[] topHits = topDocs.scoreDocs;

            for (ScoreDoc hit : topHits) {
                Document doc = searcher.doc(hit.doc);
                String result = queryEntry.getKey() + " 0 " + doc.get(FieldName.DOCNO.getName()) + " 0 " + hit.score + " STANDARD";
//            System.out.println(result);
                // add the result
                resultArr.add(result);
            }
        }
        // endregion

        // region 5. gen results
        String filename = "search_result_" + new Date().getTime();
        Path resultPath = Paths.get(filename);
        Files.write(resultPath, resultArr, StandardCharsets.UTF_8);

        System.out.println(filename + " complete!");
        // endregion
    }
}
