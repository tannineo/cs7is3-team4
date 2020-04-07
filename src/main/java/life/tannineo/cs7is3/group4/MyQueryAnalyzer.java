package life.tannineo.cs7is3.group4;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.en.EnglishMinimalStemFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.SetKeywordMarkerFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.analysis.synonym.WordnetSynonymParser;
import org.tartarus.snowball.ext.EnglishStemmer;

import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;

public class MyQueryAnalyzer extends Analyzer {

    private CharArraySet stopwords;

    private CharArraySet keywords;

    private SynonymMap synonymMap;

    public MyQueryAnalyzer() {
        this.stopwords = new CharArraySet(100, true);
        this.stopwords.addAll(Arrays.asList(CustomWordSet.customStopWords));
        this.parseSynonyms();
    }

    public MyQueryAnalyzer(CharArraySet stopwords) {
        this.stopwords = stopwords;
        this.parseSynonyms();
    }

    public MyQueryAnalyzer(CharArraySet stopwords, CharArraySet keywords) {
        this.stopwords = stopwords;
        this.keywords = keywords;
        this.parseSynonyms();
    }

    public MyQueryAnalyzer(String[] stopwords) {
        this.stopwords = new CharArraySet(300, true);
        this.stopwords.addAll(Arrays.asList(stopwords));
        this.parseSynonyms();
    }

    public MyQueryAnalyzer(String[] stopwords, String[] keywords) {
        this.stopwords = new CharArraySet(300, true);
        this.stopwords.addAll(Arrays.asList(stopwords));
        this.keywords = new CharArraySet(300, true);
        this.keywords.addAll(Arrays.asList(keywords));
        this.parseSynonyms();

    }

    private void parseSynonyms() {
        WordnetSynonymParser parser = new WordnetSynonymParser(true, true, new StandardAnalyzer());
        SynonymMap synonymMap = null;
        try {
            File file = new File(Paths.get("./wn_s.pl").toAbsolutePath().toString());
            InputStream stream = new FileInputStream(file);
            Reader rulesReader = new InputStreamReader(stream);
            parser.parse(rulesReader);
            synonymMap = parser.build();
            rulesReader.close();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.synonymMap = synonymMap;
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

        // synonyms
        tokenStream = new SynonymGraphFilter(tokenStream, this.synonymMap, true);
//        tokenStream = new FlattenGraphFilter(tokenStream);

        // snow ball filter
        tokenStream = new SnowballFilter(tokenStream, new EnglishStemmer());
        // porter stemming
        tokenStream = new PorterStemFilter(tokenStream);

        return new TokenStreamComponents(tokenizer, tokenStream);
    }
}

