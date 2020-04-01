package life.tannineo.cs7is3.group4.parser;

import com.alibaba.fastjson.JSON;
import life.tannineo.cs7is3.group4.entity.DocumentFt;
import life.tannineo.cs7is3.group4.helper.WeirdEntityParser;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class FtParser implements DocumentParser<DocumentFt> {

    public ArrayList<DocumentFt> readFile(String filePath) throws Exception {

        ArrayList<DocumentFt> parsedArr = new ArrayList<>();


        // read file
        File file = new File(filePath);

        Document doc = Jsoup.parse(file, "UTF-8");
        Elements elements = doc.getElementsByTag("DOC");

        for (Element el : elements) {
            DocumentFt docFt = new DocumentFt();
            try {
                // basic doc fields

                Elements elDOCNO = el.getElementsByTag("DOCNO");
                docFt.docNo = Entities.unescape(elDOCNO.get(0).text().trim());
                elDOCNO.get(0).text("");

                Elements elTEXT = el.getElementsByTag("TEXT");
                if (elTEXT.size() > 0) {
                    docFt.text = WeirdEntityParser.parse(Entities.unescape(elTEXT.get(0).text().trim()));
                    elTEXT.get(0).text("");
                }

                // ft custom fields

                Elements elPROFILE = el.getElementsByTag("PROFILE");
                if (elPROFILE.size() > 0) {
                    docFt.profile = WeirdEntityParser.parse(Entities.unescape(elPROFILE.get(0).text().trim()));
                    elPROFILE.get(0).text("");
                }

                Elements elDATE = el.getElementsByTag("DATE");
                if (elDATE.size() > 0) {
                    docFt.date = WeirdEntityParser.parse(Entities.unescape(elDATE.get(0).text().trim()));
                    elDATE.get(0).text("");
                }

                Elements elHEADLINE = el.getElementsByTag("HEADLINE");
                if (elHEADLINE.size() > 0) {
                    docFt.headline = WeirdEntityParser.parse(Entities.unescape(elHEADLINE.get(0).text().trim()));
                    elHEADLINE.get(0).text("");
                }

                Elements elBYLINE = el.getElementsByTag("BYLINE");
                if (elBYLINE.size() > 0) {
                    docFt.headline = WeirdEntityParser.parse(Entities.unescape(elBYLINE.get(0).text().trim()));
                    elBYLINE.get(0).text("");
                }

                Elements elPUB = el.getElementsByTag("PUB");
                if (elPUB.size() > 0) {
                    docFt.headline = WeirdEntityParser.parse(Entities.unescape(elPUB.get(0).text().trim()));
                    elPUB.get(0).text("");
                }


                Elements elPAGE = el.getElementsByTag("PAGE");
                if (elPAGE.size() > 0) {
                    docFt.headline = WeirdEntityParser.parse(Entities.unescape(elPAGE.get(0).text().trim()));
                    elPAGE.get(0).text("");
                }

                // finally the rest things goes into meta

                docFt.meta = WeirdEntityParser.parse(Entities.unescape(el.text().trim()));

//                System.out.println(JSON.toJSONString(docFt));

                parsedArr.add(docFt);

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(JSON.toJSONString(docFt));
                throw e;
            }
        }

        return parsedArr;
    }

    public ArrayList<org.apache.lucene.document.Document> toLucDoc(ArrayList<DocumentFt> arr) {
        ArrayList<org.apache.lucene.document.Document> parsedArr = new ArrayList<>();

        for (DocumentFt doc : arr) {
            org.apache.lucene.document.Document parsedDoc = new org.apache.lucene.document.Document();

            parsedDoc.add(new StringField("DOCNO", doc.docNo, Field.Store.YES));
            if (!Objects.isNull(doc.text)) {
                parsedDoc.add(new TextField("TEXT", doc.text, Field.Store.YES));
            }
            parsedDoc.add(new TextField("META", doc.meta, Field.Store.YES));

            // ft custom fields

            if (!Objects.isNull(doc.profile)) {
                parsedDoc.add(new StringField("PROFILE", doc.profile, Field.Store.YES));
            }
            if (!Objects.isNull(doc.date)) {
                parsedDoc.add(new StringField("DATE", doc.date, Field.Store.YES));
            }
            if (!Objects.isNull(doc.headline)) {
                parsedDoc.add(new TextField("HEADLINE", doc.headline, Field.Store.YES));
            }
            if (!Objects.isNull(doc.byline)) {
                parsedDoc.add(new StringField("BYLINE", doc.byline, Field.Store.YES));
            }
            if (!Objects.isNull(doc.pub)) {
                parsedDoc.add(new StringField("PUB", doc.pub, Field.Store.YES));
            }
            if (!Objects.isNull(doc.page)) {
                parsedDoc.add(new StringField("PAGE", doc.page, Field.Store.YES));
            }


            parsedArr.add(parsedDoc);
        }

        return parsedArr;
    }
}
