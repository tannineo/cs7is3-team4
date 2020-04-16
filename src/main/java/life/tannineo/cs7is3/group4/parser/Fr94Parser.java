package life.tannineo.cs7is3.group4.parser;

import com.alibaba.fastjson.JSON;
import life.tannineo.cs7is3.group4.entity.DocumentFr94;
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

public class Fr94Parser implements DocumentParser<DocumentFr94> {

	public ArrayList<DocumentFr94> readFile(String filePath) throws Exception {

		ArrayList<DocumentFr94> parsedArr = new ArrayList<>();


		// read file
		File file = new File(filePath);

		Document doc = Jsoup.parse(file, "UTF-8");
		Elements elements = doc.getElementsByTag("DOC");

		for (Element el : elements) {

			DocumentFr94 docFr94 = new DocumentFr94();
			try {
				// basic doc fields

				Elements elDOCNO = el.getElementsByTag("DOCNO");
				docFr94.docNo = Entities.unescape(elDOCNO.get(0).text().trim());
				elDOCNO.get(0).text("");

				Elements elTEXT = el.getElementsByTag("TEXT");
				docFr94.text = WeirdEntityParser.parse(Entities.unescape(elTEXT.get(0).text().trim()));
				elTEXT.get(0).text("");

				// fr94 custom fields

				Elements elPARENT = el.getElementsByTag("PARENT");
				if (elPARENT.size() > 0) {
					docFr94.parent = WeirdEntityParser.parse(Entities.unescape(elPARENT.get(0).text().trim()));
					elPARENT.get(0).text("");
				}

				Elements elDOCTITLE = el.getElementsByTag("DOCTITLE");
				if (elDOCTITLE.size() > 0) {
					docFr94.docTitle = WeirdEntityParser.parse(Entities.unescape(elDOCTITLE.get(0).text().trim()));
					elDOCTITLE.get(0).text("");
				}

				// finally the rest things goes into meta

				docFr94.meta = WeirdEntityParser.parse(Entities.unescape(el.text().trim()));

//              System.out.println(JSON.toJSONString(docFr94));

				parsedArr.add(docFr94);

			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(JSON.toJSONString(docFr94));
				throw e;
			}
		}

		return parsedArr;
	}

	public ArrayList<org.apache.lucene.document.Document> toLucDoc(ArrayList<DocumentFr94> arr) {
		ArrayList<org.apache.lucene.document.Document> parsedArr = new ArrayList<>();

		for (DocumentFr94 doc : arr) {
			org.apache.lucene.document.Document parsedDoc = new org.apache.lucene.document.Document();

			parsedDoc.add(new StringField("DOCNO", doc.docNo, Field.Store.YES));
			if (!Objects.isNull(doc.text)) {
				parsedDoc.add(new TextField("TEXT", doc.text, Field.Store.YES));
			}
			parsedDoc.add(new TextField("META", doc.meta, Field.Store.YES));

			// fr94 custom fields

			if (!Objects.isNull(doc.parent)) {
				parsedDoc.add(new StringField("PARENT", doc.parent, Field.Store.YES));
			}

			if (!Objects.isNull(doc.docTitle)) {
				parsedDoc.add(new TextField("HEADLINE", doc.docTitle, Field.Store.YES)); // DOCTITLE
			}

			parsedArr.add(parsedDoc);
		}

		return parsedArr;
	}
}
