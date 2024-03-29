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
	private static final String CLASSIC = "Classic";
	private static final String BM25 = "BM25";
	private static final String BOOLEAN = "Boolean";
	private static final String LMDIRICHLET = "LMDirichlet";
	private static final String LMJELINEKMERCER = "LMJelinekMercer";

	public static final String[] ALL_SCORING_MODELS = {CLASSIC, BM25, LMDIRICHLET, LMJELINEKMERCER};

	// Analysers
	private static final String STANDARD = "Standard";
	private static final String ENGLISH = "English";
	private static final String CUSTOM1 = "Custom1";
	private static final String CUSTOM2 = "Custom2";
	private static final String CUSTOM3 = "Custom3";
	public static final String[] ALL_ANALYZERS = {STANDARD, ENGLISH, CUSTOM1, CUSTOM2, CUSTOM3};


	/** 
	 * Check passed argument is valid analyzer name
	 * @param analyzer
	 * @return
	 */
	public static boolean isValidAnalyzer(String analyzer) {
		for (String Model : ALL_ANALYZERS) {
			if (analyzer.equals(Model)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check passed argument is valid scoring model name
	 * @param rankModel
	 * @return
	 */
	public static boolean isValidScoringModel(String rankModel) {
		for(String Model : ALL_SCORING_MODELS) {
			if(rankModel.equals(Model)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Gives scoring model object based on model name passed
	 * @param model
	 * @return scoring model object
	 */
	public static Similarity getScoringModel(String model) { //CLASSIC, BM25, LM_DIRICHLET, LMJELINEKMERCER
		Similarity similarity = null;
		switch (model){
		case CLASSIC:
			similarity = new ClassicSimilarity();
			break;
		case BM25:
			similarity = new BM25Similarity();
			break;
		case LMDIRICHLET:
			similarity = new LMDirichletSimilarity();
			break;
		case LMJELINEKMERCER:
			similarity = new LMJelinekMercerSimilarity(0.49f);
			break;
		}
		return similarity;
	}

	/**
	 * gives analyzer object based on model name passed
	 * @param model
	 * @return analyzer object
	 * @throws Exception
	 */
	public static Analyzer getAnalyzer(String model) throws Exception { //STANDARD, ENGLISH, CUSTOM1, CUSTOM2, CUSTOM3
		Analyzer analyzer=null;
		switch (model){
		case STANDARD:
			analyzer = new StandardAnalyzer(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
			break;
		case ENGLISH:
			analyzer = new EnglishAnalyzer();
			break;
		case CUSTOM1:
			analyzer = new MyAnalyzer();
			break;
		case CUSTOM2:
			analyzer = new MySynonymAnalyzer();
			break;
		case CUSTOM3:
			analyzer = new CustomAnalyzer_Syn_stp();
			break;
		default:
			throw new Exception(String.format("ERROR: Invalid analyzer: %s", analyzer));
		}
		return analyzer;
	}

}
