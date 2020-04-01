package life.tannineo.cs7is3.group4;

import life.tannineo.cs7is3.group4.parser.FbisParser;
import life.tannineo.cs7is3.group4.parser.Fr94Parser;
import life.tannineo.cs7is3.group4.parser.FtParser;
import life.tannineo.cs7is3.group4.parser.LatimesParser;
import org.apache.lucene.document.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class App {

    private static String[] corporaName = {"fbis", "fr94", "ft", "latimes"};

    public static void main(String args[]) throws Exception {

        // region 1. parse corpora

//        FbisParser fbisParser = new FbisParser();
//        fbisParser.readFile("../corpora/fbis/fb396001.sgm");
//        Fr94Parser fr94Parser = new Fr94Parser();
//        fr94Parser.readFile("../corpora/fr94/fr940105_1.sgm");
//        FtParser ftParser = new FtParser();
//        ftParser.readFile("../corpora/ft/ft911_1.sgm");
//        LatimesParser latimesParser = new LatimesParser();
//        latimesParser.readFile("../corpora/latimes/la010189.sgm");

        ArrayList<Document> documents = new ArrayList<>();

        File FbisFolder = new File("../corpora/fbis");
        FbisParser fbisParser = new FbisParser();
        for (File sgm : Objects.requireNonNull(FbisFolder.listFiles())) {
            if (sgm.isFile() && sgm.getAbsolutePath().endsWith(".sgm")) {
                ArrayList<Document> arr = fbisParser.toLucDoc(fbisParser.readFile(sgm.getAbsolutePath()));
                documents.addAll(arr);
            }
        }

        File Fr94Folder = new File("../corpora/fr94");
        Fr94Parser fr94Parser = new Fr94Parser();
        for (File sgm : Objects.requireNonNull(Fr94Folder.listFiles())) {
            if (sgm.isFile() && sgm.getAbsolutePath().endsWith(".sgm")) {
                ArrayList<Document> arr = fr94Parser.toLucDoc(fr94Parser.readFile(sgm.getAbsolutePath()));
                documents.addAll(arr);
            }
        }

        File FtFolder = new File("../corpora/ft");
        FtParser ftFolder = new FtParser();
        for (File sgm : Objects.requireNonNull(FtFolder.listFiles())) {
            if (sgm.isFile() && sgm.getAbsolutePath().endsWith(".sgm")) {
                ArrayList<Document> arr = ftFolder.toLucDoc(ftFolder.readFile(sgm.getAbsolutePath()));
                documents.addAll(arr);
            }
        }

        File LatimesFolder = new File("../corpora/fbis");
        LatimesParser latimesParser = new LatimesParser();
        for (File sgm : Objects.requireNonNull(LatimesFolder.listFiles())) {
            if (sgm.isFile() && sgm.getAbsolutePath().endsWith(".sgm")) {
                ArrayList<Document> arr = latimesParser.toLucDoc(latimesParser.readFile(sgm.getAbsolutePath()));
                documents.addAll(arr);
            }
        }

        System.out.println("Parsed all documents... " + documents.size() + " documents in total!");

        // endregion

        // region 2. indexing

        // endregion

        // region 3. parse queries
//        QueryParser queryParser = new QueryParser();
//        ArrayList<DocumentQuery> documentQueries = queryParser.readQueries("topics");
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
