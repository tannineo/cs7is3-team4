package life.tannineo.cs7is3.group4.parser;

import life.tannineo.cs7is3.group4.entity.DocumentQuery;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;

public class CustomQueryParser {
    public ArrayList<DocumentQuery> readQueries(String queryFilePath) {
        ArrayList<DocumentQuery> QueryArray = new ArrayList<>();
        try {
            File file = new File(queryFilePath);
            Document doc = Jsoup.parse(file, "UTF-8");
            Elements elements = doc.getElementsByTag("top");
            for (Element el : elements) {
                DocumentQuery documentQuery = new DocumentQuery();
                Elements QueryId = el.getElementsByTag("num");
                Elements Title = el.getElementsByTag("title");
                Elements Desc = el.getElementsByTag("desc");
                Elements Narrative = el.getElementsByTag("narr");

                documentQuery.queryId = QueryId.text().split(" ")[1];
                documentQuery.title = Title.text().replaceAll("[^a-zA-Z ]", "").toLowerCase();
                documentQuery.description = Desc.text().split("Narrative")[0].replace("Description:", "").replaceAll("[^a-zA-Z ]", "").toLowerCase();
                documentQuery.narrative = Narrative.text().replace("Narrative:", "").replaceAll("[^a-zA-Z ]", "").toLowerCase();
                QueryArray.add(documentQuery);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return QueryArray;
    }
}
