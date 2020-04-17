package life.tannineo.cs7is3.group4;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.Similarity;

public class AppUtility {
	// Supported Ranking models
	private static final String CLASSIC = "CLASSIC";
	private static final String BM25 = "BM25";
	private static final String BOOLEAN = "BOOLEAN";
	private static final String LM_DIRICHLET = "LM_DIRICHLET";
	private static final String LMJELINEKMERCER = "LMJELINEKMERCER"; //LMJelinekMercerSimilarity

	private static final String[] ALL_SCORING_MODELS = {CLASSIC, BM25, LM_DIRICHLET, LMJELINEKMERCER};

	// Analysers
	private static final String STANDARD = "STANDARD";
	private static final String ENGLISH = "ENGLISH";
	private static final String CUSTOM1 = "CUSTOM1";
	private static final String CUSTOM2 = "CUSTOM2";
	private static final String CUSTOM3 = "CUSTOM3";
	private static final String[] ALL_ANALYZERS = {STANDARD, ENGLISH, CUSTOM1, CUSTOM2, CUSTOM3};


	public static boolean isValidAnalyzer(String analyzer) {
		for (String Model : ALL_ANALYZERS) {
			if (analyzer.equals(Model)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isValidScoringModel(String rankModel) {
		for(String Model : ALL_SCORING_MODELS) {
			if(rankModel.equals(Model)){
				return true;
			}
		}
		return false;
	}

	public static Similarity getScoringModel(String model) { //CLASSIC, BM25, LM_DIRICHLET, LMJELINEKMERCER
		Similarity similarity = null;
		switch (model){
		case "CLASSIC":
			similarity = new ClassicSimilarity();
			break;
		case "BM25":
			similarity = new BM25Similarity();
			break;
		case "LM_DIRICHLET":
			similarity = new LMDirichletSimilarity();
			break;
		case "LMJELINEKMERCER":
			similarity = new LMJelinekMercerSimilarity(0.49f);
			break;
		}
		return similarity;
	}

	public static Analyzer getAnalyzer(String model) throws Exception { //STANDARD, ENGLISH, CUSTOM1, CUSTOM2, CUSTOM3
		Analyzer analyzer=null;
		switch (model){
		case "STANDARD":
			analyzer = new StandardAnalyzer(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
			break;
		case "ENGLISH":
			analyzer = new EnglishAnalyzer();
			break;
		case "CUSTOM1":
			analyzer = new MyAnalyzer();
			break;
		case "CUSTOM2":
			analyzer = new MySynonymAnalyzer();
			break;
		case "CUSTOM3":
			analyzer = new CustomAnalyzer_Syn_stp();
			break;
		default:
			throw new Exception(String.format("ERROR: Invalid analyzer: %s", analyzer));
		}
		return analyzer;
	}

}
