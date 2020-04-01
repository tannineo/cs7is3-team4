package life.tannineo.cs7is3.group4;

import life.tannineo.cs7is3.group4.entity.DocumentQuery;
import life.tannineo.cs7is3.group4.parser.QueryParser;

import java.io.IOException;
import java.util.ArrayList;

public class App {

    private static String[] corporaName = {"fbis", "fr94", "ft", "latimes"};

    public static void main(String args[]) throws IOException {
        // region 1. parse corpora

        // endregion

        // region 2. indexing

        // endregion

        // region 3. parse queries
        QueryParser queryParser = new QueryParser();
        ArrayList<DocumentQuery> documentQueries = queryParser.readQueries("topics");
//        for (DocumentQuery documentQuery : documentQueries) {
//            System.out.println(documentQuery.Title);
//            System.out.println(documentQuery.description);
//            System.out.println(documentQuery.Narrative);
//        }
        // endregion

        // region 4. search

        // endregion

        // region 5. gen results

        // endregion
    }
}
