package life.tannineo.cs7is3.group4;

import java.io.IOException;
import java.util.ArrayList;

public class App {

    private static String[] corporaName = {"fbis", "fr94", "ft", "latimes"};

    public static void main(String args[]) throws IOException {
        // region 1. parse corpora
        System. out. println("Hello World");
        // endregion

        // region 2. indexing

        // endregion

        // region 3. parse queries
        QueryParser queryParser = new QueryParser();
        ArrayList<DocumentQuery> documentQueries = queryParser.readQueries("D:/Sem_2/Information_Retrival_and_web_search/Assignment-2/cs7is3-team4/Query_topic/topics");
        for (DocumentQuery documentQuery: documentQueries) {
            System. out. println(documentQuery.Title);
            System. out. println(documentQuery.description);
            System. out. println(documentQuery.Narrative);

        }
        // endregion

        // region 4. search

        // endregion

        // region 5. gen results

        // endregion
    }
}
