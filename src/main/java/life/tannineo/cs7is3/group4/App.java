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
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
    
		Analyzer mCustomAnalyzer_Syn_stp = new CustomAnalyzer_Syn_stp(); /* use mCustomAnalyzer_Syn_stp */
		// endregion 0

		// region 1 + 2. corpora parsing & indexing
		//set flag to use old indexes and to save time, toIndex = flag
		boolean toIndex = true;
		if (toIndex) {

//			IndexWriterConfig iwconfig = new IndexWriterConfig(myAnalyzer);
			IndexWriterConfig iwconfig = new IndexWriterConfig(mCustomAnalyzer_Syn_stp); /* use mCustomAnalyzer_Syn_stp */

			iwconfig.setSimilarity(bm25Similarity);
			Directory indexDir = FSDirectory.open(Paths.get(App.INDEX_PATH));
			iwconfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE); // overwrite multiple times ???
			IndexWriter indexWriter = new IndexWriter(indexDir, iwconfig);

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
		}
		// endregion 1 + 2
		
//		// region 3. query parsing
//		LinkedHashMap<String, Query> queryLinkedHashMap = new LinkedHashMap<>();
//		parse_query(mCustomAnalyzer_Syn_stp, queryLinkedHashMap);
//		// endregion 3
//		// region 4. query searching
//		search_query(bm25Similarity,queryLinkedHashMap);
//		// endregion 4
		
		//New query parsing and search 
		//author: Vishal
		// region 3 +4 . query parsing + searching
		parse_search_query(bm25Similarity, mCustomAnalyzer_Syn_stp); 
		// endregion 3 + 4

		long end = System.currentTimeMillis();
		NumberFormat secFormatter = new DecimalFormat("#0.00000");
		System.out.println("Program Running time is " + secFormatter.format((end - start) / 1000d) + " seconds");
	}

	private static void parse_query(Analyzer mCustomAnalyzer_Syn_stp, LinkedHashMap<String, Query> queryLinkedHashMap) throws ParseException {
		ArrayList<DocumentQuery> documentQueries = new CustomQueryParser().readQueries("topics");

		// parameter tuning
		HashMap<String, Float> boosts = new HashMap<>();

		// removed k-stem and snowball using these MAP = 0.3010
		boosts.put(FieldName.TEXT.getName(), 40f);
		boosts.put(FieldName.GRAPHIC.getName(), 3f);
		boosts.put(FieldName.HEADLINE.getName(), 2f);
		boosts.put(FieldName.HEADER.getName(), 2f);
		boosts.put(FieldName.HT.getName(), 5f);


//		MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(FieldName.getAllNamesExceptNonSense(), mySynonymAnalyzer, boosts);
		MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(FieldName.getAllNamesExceptNonSense(), mCustomAnalyzer_Syn_stp, boosts); /* use mCustomAnalyzer_Syn_stp */
//		multiFieldQueryParser.setAllowLeadingWildcard(true);
		for (DocumentQuery dq : documentQueries) {
			System.out.println("Parsing Query ID:" + dq.queryId);
//  	    String parsedQueryStr = dq.title + " " + dq.description;
			String parsedQueryStr = dq.title + " " + dq.description + " " + dq.narrative;
//	        String parsedQueryStr = MyQueryStringParser.parseQueryString(dq.title + " " + dq.description + " " + dq.narrative);
			Query qry = multiFieldQueryParser.parse(parsedQueryStr);
			System.out.println(qry.toString());
			queryLinkedHashMap.put(dq.queryId, qry);
		}

		System.out.println("Parsed all queries... " + queryLinkedHashMap.entrySet().size() + " in total!");
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
//              System.out.println(result);
				// add the result
				resultArr.add(result);
			}
		}

		// region 5. gen results
		String filename = "search_result_" + new Date().getTime();
		Path resultPath = Paths.get(filename);
		Files.write(resultPath, resultArr, StandardCharsets.UTF_8);

		System.out.println(filename + " complete!");
		// endregion 5
	}
	private static void parse_search_query(BM25Similarity bm25Similarity, Analyzer mCustomAnalyzer_Syn_stp) throws ParseException, IOException {

		Directory dirr = FSDirectory.open(Paths.get(App.INDEX_PATH));
		DirectoryReader dirReader = DirectoryReader.open(dirr);
		IndexSearcher indexSearcher = new IndexSearcher(dirReader);
		indexSearcher.setSimilarity(bm25Similarity);

		PrintWriter writer = new PrintWriter("new_queryResults", "UTF-8");

		ArrayList<DocumentQuery> documentQueries = new CustomQueryParser().readQueries("topics");
		HashMap<String, Float> boosts = new HashMap<>();

		// search_result_1586960735098 MAP = 0.3200
		boosts.put(FieldName.HEADLINE.getName(), (float) 0.1);
		boosts.put(FieldName.TEXT.getName(), (float) 0.9);

		MultiFieldQueryParser multiFieldQueryParser = new MultiFieldQueryParser(FieldName.getAllNamesExceptNonSense(), mCustomAnalyzer_Syn_stp, boosts); /* use mCustomAnalyzer_Syn_stp */

		for (DocumentQuery dq : documentQueries) {
			System.out.print("Parsing Query ID:" + dq.queryId + " . ");
//			System.out.println(dq.title + " " + dq.description + " " + dq.narrative);

			List<String> splitNarrative = groupNarrativeRelevencey(dq.narrative);
			String relevantNarr = splitNarrative.get(0).trim();

			BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();

			if (dq.title.length() > 0) {
				Query titleQuery = multiFieldQueryParser.parse(QueryParser.escape(dq.title));
				Query descriptionQuery = multiFieldQueryParser.parse(QueryParser.escape(dq.description));
				Query narrativeQuery = null;
				if(relevantNarr.length()>0) {
					narrativeQuery = multiFieldQueryParser.parse(QueryParser.escape(relevantNarr));
				}
				booleanQuery.add(new BoostQuery(titleQuery, (float) 4), BooleanClause.Occur.SHOULD);
				booleanQuery.add(new BoostQuery(descriptionQuery, (float) 2), BooleanClause.Occur.SHOULD);
				if (narrativeQuery != null) {
					booleanQuery.add(new BoostQuery(narrativeQuery, (float) 1.2), BooleanClause.Occur.SHOULD);
				}
				//search
				System.out.println("Searching Query...");
				ScoreDoc[] hits = indexSearcher.search(booleanQuery.build(), HITS_PER_PAGE).scoreDocs;

				for (int hitIndex = 0; hitIndex < hits.length; hitIndex++) {
					ScoreDoc hit = hits[hitIndex];

					writer.println(dq.queryId + " 0 " + indexSearcher.doc(hit.doc).get(FieldName.DOCNO.getName()) + " " + hitIndex + " " + hit.score + " 0 ");					
				}            	
			}
		}
		closeIndexReader(dirReader);
		closePrintWriter(writer);
		System.out.println("Parsed all queries... " + documentQueries.size() + " in total!");
		System.out.println("queries executed");
	}

	/** 
	 * Create a list of 2 string as relevant and non relevant for each 
	 * query by filtering 'not relevant' or 'relevant'phrases which not 
	 * not useful.  
	 * @param narStr- narrative string present in current query
	 * @return
	 */
	private static List<String> groupNarrativeRelevencey(String narStr) {
		StringBuilder relevantNarrStr = new StringBuilder();
		StringBuilder irrelevantNarrStr = new StringBuilder();
		String[] narStrSplited = narStr.toLowerCase().split("\\.");
		List<String> result = new ArrayList<String>();
		for (String curStr : narStrSplited) {
			if (!curStr.contains("not relevant") && !curStr.contains("irrelevant")) {
				String usefulStr = curStr.replaceAll("a relevant document|"
						+ "a document will|to be relevant|"
						+ "relevant documents|"
						+ "a document must|"
						+ "relevant|"
						+ "will contain|"
						+ "will discuss|"
						+ "will provide|"
						+ "must cite",
						"");
				relevantNarrStr.append(usefulStr);
			} else {
				String re = curStr.replaceAll("are also not relevant|"
						+ "are not relevant|"
						+ "are irrelevant|"
						+ "is not relevant", "");
				irrelevantNarrStr.append(re);
			}
		}
		result.add(relevantNarrStr.toString());
		result.add(irrelevantNarrStr.toString());
		return result;
	}


	private static void closeIndexReader(DirectoryReader dirReader) {
		try {
			dirReader.close();
		} catch (IOException e) {
			System.out.println("ERROR: an error occurred when closing the index from the directory!");
			System.out.println(String.format("ERROR MESSAGE: %s", e.getMessage()));
		}
	}

	private static void closePrintWriter(PrintWriter writer){
		writer.flush();
		writer.close();
	}


}
