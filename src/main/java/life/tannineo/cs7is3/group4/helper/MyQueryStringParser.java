package life.tannineo.cs7is3.group4.helper;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyQueryStringParser {

    public static String parseQueryString(String keywords) throws Exception {

        List<String> result = new ArrayList<>();

        StandardAnalyzer analyzer = new StandardAnalyzer();

        try {
            TokenStream stream = analyzer.tokenStream(null, new StringReader(keywords));
            stream.reset();
            while (stream.incrementToken()) {
                result.add(stream.getAttribute(CharTermAttribute.class).toString());
            }
            stream.close();
        } catch (Exception e) {
            throw e;
        }

        // remove duplicates
        Set<String> keywordSet = new HashSet<>();
        for (String str : result) {
            for (String strr : str.split(" ")) {
                if (!str.equals("_")) keywordSet.add(strr);
            }
        }

        // construct new string
        StringBuilder resultString = new StringBuilder();
        for (String str : keywordSet) {
            if (!str.equals("_")) resultString.append(str).append(" ");
        }


        return resultString.toString();
    }
}
