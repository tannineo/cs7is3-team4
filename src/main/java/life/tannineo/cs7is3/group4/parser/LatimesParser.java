package life.tannineo.cs7is3.group4.parser;

import com.alibaba.fastjson.JSON;
import life.tannineo.cs7is3.group4.entity.DocumentLatimes;
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

public class LatimesParser implements DocumentParser<DocumentLatimes> {

    public ArrayList<DocumentLatimes> readFile(String filePath) throws Exception {

        ArrayList<DocumentLatimes> parsedArr = new ArrayList<>();


        // read file
        File file = new File(filePath);

        Document doc = Jsoup.parse(file, "UTF-8");
        Elements elements = doc.getElementsByTag("DOC");


        for (Element el : elements) {
            DocumentLatimes docLatimes = new DocumentLatimes();
            try {
                // basic doc fields

                Elements elDOCNO = el.getElementsByTag("DOCNO");
                docLatimes.docNo = Entities.unescape(elDOCNO.get(0).text().trim());
                elDOCNO.get(0).text("");

                Elements elTEXT = el.getElementsByTag("TEXT");
                if (elTEXT.size() > 0) {
                    docLatimes.text = WeirdEntityParser.parse(Entities.unescape(elTEXT.get(0).text().trim()));
                    elTEXT.get(0).text("");
                }

                // ft custom fields

                Elements elDOCID = el.getElementsByTag("DOCID");
                if (elDOCID.size() > 0) {
                    docLatimes.docId = WeirdEntityParser.parse(Entities.unescape(elDOCID.get(0).text().trim()));
                    elDOCID.get(0).text("");
                }

                Elements elDATE = el.getElementsByTag("DATE");
                if (elDATE.size() > 0) {
                    docLatimes.date = WeirdEntityParser.parse(Entities.unescape(elDATE.get(0).text().trim()));
                    elDATE.get(0).text("");
                }

                Elements elSECTION = el.getElementsByTag("SECTION");
                if (elSECTION.size() > 0) {
                    docLatimes.section = WeirdEntityParser.parse(Entities.unescape(elSECTION.get(0).text().trim()));
                    elSECTION.get(0).text("");
                }

                Elements elLENGTH = el.getElementsByTag("LENGTH");
                if (elLENGTH.size() > 0) {
                    docLatimes.length = WeirdEntityParser.parse(Entities.unescape(elLENGTH.get(0).text().trim()));
                    elLENGTH.get(0).text("");
                }

                Elements elHEADLINE = el.getElementsByTag("HEADLINE");
                if (elHEADLINE.size() > 0) {
                    docLatimes.headline = WeirdEntityParser.parse(Entities.unescape(elHEADLINE.get(0).text().trim()));
                    elHEADLINE.get(0).text("");
                }


                Elements elGRAPHIC = el.getElementsByTag("GRAPHIC");
                if (elGRAPHIC.size() > 0) {
                    docLatimes.graphic = WeirdEntityParser.parse(Entities.unescape(elGRAPHIC.get(0).text().trim()));
                    elGRAPHIC.get(0).text("");
                }

                Elements elTYPE = el.getElementsByTag("TYPE");
                if (elTYPE.size() > 0) {
                    docLatimes.type = WeirdEntityParser.parse(Entities.unescape(elTYPE.get(0).text().trim()));
                    elTYPE.get(0).text("");
                }

                Elements elSUBJECT = el.getElementsByTag("SUBJECT");
                if (elSUBJECT.size() > 0) {
                    docLatimes.subject = WeirdEntityParser.parse(Entities.unescape(elSUBJECT.get(0).text().trim()));
                    elSUBJECT.get(0).text("");
                }

                Elements elBYLINE = el.getElementsByTag("BYLINE");
                if (elBYLINE.size() > 0) {
                    docLatimes.byline = WeirdEntityParser.parse(Entities.unescape(elBYLINE.get(0).text().trim()));
                    elBYLINE.get(0).text("");
                }

                // finally the rest things goes into meta

                docLatimes.meta = WeirdEntityParser.parse(Entities.unescape(el.text().trim()));

//                System.out.println(JSON.toJSONString(docLatimes));

                parsedArr.add(docLatimes);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(JSON.toJSONString(doc));
                throw e;
            }
        }


        return parsedArr;
    }

    public ArrayList<org.apache.lucene.document.Document> toLucDoc(ArrayList<DocumentLatimes> arr) {
        ArrayList<org.apache.lucene.document.Document> parsedArr = new ArrayList<>();

        for (DocumentLatimes doc : arr) {
            org.apache.lucene.document.Document parsedDoc = new org.apache.lucene.document.Document();

            parsedDoc.add(new StringField("DOCNO", doc.docNo, Field.Store.YES));
            if (!Objects.isNull(doc.text)) {
                parsedDoc.add(new TextField("TEXT", doc.text, Field.Store.YES));
            }
            parsedDoc.add(new TextField("META", doc.meta, Field.Store.YES));

            // latimes custom fields
            if (!Objects.isNull(doc.docId)) {
                parsedDoc.add(new StringField("DOCID", doc.docId, Field.Store.YES));
            }
            if (!Objects.isNull(doc.date)) {
                parsedDoc.add(new StringField("DATE", doc.date, Field.Store.YES));
            }
            if (!Objects.isNull(doc.section)) {
                parsedDoc.add(new StringField("SECTION", doc.section, Field.Store.YES));
            }
            if (!Objects.isNull(doc.length)) {
                parsedDoc.add(new StringField("LENGTH", doc.length, Field.Store.YES));
            }
            if (!Objects.isNull(doc.headline)) {
                parsedDoc.add(new TextField("HEADLINE", doc.headline, Field.Store.YES));
            }
            if (!Objects.isNull(doc.graphic)) {
                parsedDoc.add(new TextField("GRAPHIC", doc.graphic, Field.Store.YES));
            }
            if (!Objects.isNull(doc.type)) {
                parsedDoc.add(new StringField("TYPE", doc.type, Field.Store.YES));
            }
            if (!Objects.isNull(doc.subject)) {
                parsedDoc.add(new StringField("SUBJECT", doc.subject, Field.Store.YES));
            }
            if (!Objects.isNull(doc.byline)) {
                parsedDoc.add(new StringField("BYLINE", doc.byline, Field.Store.YES));
            }

            parsedArr.add(parsedDoc);
        }

        return parsedArr;
    }

}
