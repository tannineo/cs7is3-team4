package life.tannineo.cs7is3.group4;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.en.EnglishMinimalStemFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.util.Arrays;

public class MyAnalyzer extends Analyzer {

    private CharArraySet stopwords;

    private CharArraySet keywords;

    public MyAnalyzer() {
        this.stopwords = new CharArraySet(100, true);
        this.stopwords.addAll(Arrays.asList(CustomWordSet.customStopWords));
    }

    public MyAnalyzer(CharArraySet stopwords) {
        this.stopwords = stopwords;
    }

    public MyAnalyzer(CharArraySet stopwords, CharArraySet keywords) {
        this.stopwords = stopwords;
        this.keywords = keywords;
    }

    public MyAnalyzer(String[] stopwords) {
        this.stopwords = new CharArraySet(300, true);
        this.stopwords.addAll(Arrays.asList(stopwords));
    }

    public MyAnalyzer(String[] stopwords, String[] keywords) {
        this.stopwords = new CharArraySet(300, true);
        this.stopwords.addAll(Arrays.asList(stopwords));
        this.keywords = new CharArraySet(300, true);
        this.keywords.addAll(Arrays.asList(keywords));
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer tokenizer = new StandardTokenizer();

        TokenStream tokenStream = new LowerCaseFilter(tokenizer);
        // a
        tokenStream = new TrimFilter(tokenStream);
        // remove 's
        tokenStream = new EnglishPossessiveFilter(tokenStream);

        if (this.keywords != null && this.keywords.size() > 0) {
            // key word filter
            tokenStream = new SetKeywordMarkerFilter(tokenStream, this.keywords);
        }

        // minimal plural stemmer
        tokenStream = new EnglishMinimalStemFilter(tokenStream);
        // remove stopwords
        tokenStream = new StopFilter(tokenStream, this.stopwords);

        // snow ball filter
        tokenStream = new SnowballFilter(tokenStream, new EnglishStemmer());
        // porter stemming
        tokenStream = new PorterStemFilter(tokenStream);
//        // k stemming
//        tokenStream = new KStemFilter(tokenStream);
//        // n-gram
//        filtered = new ShingleFilter(filtered, 2, 3);

        return new TokenStreamComponents(tokenizer, tokenStream);
    }

}

