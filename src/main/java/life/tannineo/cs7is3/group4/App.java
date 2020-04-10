package life.tannineo.cs7is3.group4;

import life.tannineo.cs7is3.group4.entity.DocumentQuery;
import life.tannineo.cs7is3.group4.parser.*;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class App {

    static String INDEX_PATH = "generated_index";
    static int HITS_PER_PAGE = 1000;

    private static String[] corporaName = {"fbis", "fr94", "ft", "latimes"};

    public static void main(String args[]) throws Exception {

        // region 0. prepare and test field
        // TODO: replace the analyzer & similarity with custom one!
        BM25Similarity bm25Similarity = new BM25Similarity();
//        CharArraySet customStopWordSet = new CharArraySet(127, true);
//        customStopWordSet.addAll(Arrays.asList(CustomWordSet.customStopWords));
//        CharArraySet cusromNotToStemSet = new CharArraySet(100, true);
//        cusromNotToStemSet.addAll(Arrays.asList(CustomWordSet.customWordsNotToStem));
//        EnglishAnalyzer englishAnalyzer = new EnglishAnalyzer(customStopWordSet, cusromNotToStemSet);
        MyAnalyzer myAnalyzer = new MyAnalyzer(CustomWordSet.extraLongStopwordList, CustomWordSet.customWordsNotToStem);
        // endregion

        // region 1 + 2. corpora parsing & indexing

        IndexWriterConfig iwconfig = new IndexWriterConfig(myAnalyzer);
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

        indexWriter.close();
        indexDir.close(); // close index before next use

        System.out.println("Indexed all documents... index files generated in generated_index/");

        // endregion

        // region 3. query parsing
        ArrayList<DocumentQuery> documentQueries = new QueryParser().readQueries("topics");
//        for (DocumentQuery documentQuery : documentQueries) {
//            System.out.println(documentQuery.title);
//            System.out.println(documentQuery.description);
//            System.out.println(documentQuery.narrative);
//        }
        LinkedHashMap<String, Query> queryLinkedHashMap = new LinkedHashMap<>();
		String[] relevantFields = {"TEXT"};
        MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(relevantFields, myAnalyzer);
        // multiFieldQueryParser.setAllowLeadingWildcard(true);
        for (DocumentQuery dq : documentQueries) {
            System.out.println("Parsing Query ID:" + dq.queryId);
            String parsedQueryStr = dq.description;
//            String parsedQueryStr = MyQueryStringParser.parseQueryString(myAnalyzer, dq.title + " " + dq.description + " " + dq.narrative);
//            System.out.println(parsedQueryStr);
            Query qry = multiFieldQueryParser.parse(parsedQueryStr);
            queryLinkedHashMap.put(dq.queryId, qry);
        }

        System.out.println("Parsed all queries... " + queryLinkedHashMap.entrySet().size() + " in total!");
        // endregion

        // region 4. search
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
//                System.out.println(result);
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
