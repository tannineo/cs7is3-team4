package life.tannineo.cs7is3.group4.parser;

import com.alibaba.fastjson.JSON;
import life.tannineo.cs7is3.group4.entity.DocumentFbis;
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

public class FbisParser implements DocumentParser<DocumentFbis> {

	public ArrayList<DocumentFbis> readFile(String filePath) throws Exception {

		ArrayList<DocumentFbis> parsedArr = new ArrayList<>();


		// read file
		File file = new File(filePath);

		Document doc = Jsoup.parse(file, "UTF-8");
		Elements elements = doc.getElementsByTag("DOC");

		for (Element el : elements) {
			DocumentFbis docFbis = new DocumentFbis();

			try {
				// take element one bv one, after extracting the content: replace them with empty strings!

				// basic doc fields

				Elements elDOCNO = el.getElementsByTag("DOCNO");
				docFbis.docNo = Entities.unescape(elDOCNO.get(0).text().trim());
				elDOCNO.get(0).text("");

				Elements elTEXT = el.getElementsByTag("TEXT");
				docFbis.text = WeirdEntityParser.parse(Entities.unescape(elTEXT.get(0).text().trim()));
				elTEXT.get(0).text("");

				// fbis custom fields

				Elements elHT = el.getElementsByTag("HT");
				if (elHT.size() > 0) {
					docFbis.ht = WeirdEntityParser.parse(Entities.unescape(elHT.get(0).text().trim()));
					elHT.get(0).text("");
				}

				Elements elDate1 = el.getElementsByTag("DATE1");
				if (elDate1.size() > 0) {
					docFbis.date1 = WeirdEntityParser.parse(Entities.unescape(elDate1.get(0).text().trim()));
					elDate1.get(0).text("");
				}

				Elements elTI = el.getElementsByTag("TI");
				if (elTI.size() > 0) {
					docFbis.ti = WeirdEntityParser.parse(Entities.unescape(elTI.get(0).text().trim()));
					elTI.get(0).text("");
				}

				Elements elHEADER = el.getElementsByTag("HEADER");
				if (elHEADER.size() > 0) {
					docFbis.header = WeirdEntityParser.parse(Entities.unescape(elHEADER.get(0).text().trim()));
					elHEADER.get(0).text("");
				}

				// finally the rest things goes into meta
				docFbis.meta = WeirdEntityParser.parse(Entities.unescape(el.text().trim()));

//              System.out.println(JSON.toJSONString(docFbis));

				parsedArr.add(docFbis);

			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(JSON.toJSONString(docFbis));
				throw e;
			}
		}

		return parsedArr;
	}

	public ArrayList<org.apache.lucene.document.Document> toLucDoc(ArrayList<DocumentFbis> arr) {
		ArrayList<org.apache.lucene.document.Document> parsedArr = new ArrayList<>();

		for (DocumentFbis doc : arr) {
			org.apache.lucene.document.Document parsedDoc = new org.apache.lucene.document.Document();

			parsedDoc.add(new StringField("DOCNO", doc.docNo, Field.Store.YES));
			if (!Objects.isNull(doc.text)) {
				parsedDoc.add(new TextField("TEXT", doc.text, Field.Store.YES));
			}
			parsedDoc.add(new TextField("META", doc.meta, Field.Store.YES));

			// fbis custom fields

			if (!Objects.isNull(doc.date1)) {
				parsedDoc.add(new StringField("DATE", doc.date1, Field.Store.YES)); // date1
			}
			if (!Objects.isNull(doc.ti)) {
				parsedDoc.add(new TextField("HEADLINE", doc.ti, Field.Store.YES));  // ti
			}
			if (!Objects.isNull(doc.ht)) {
				parsedDoc.add(new StringField("HT", doc.ht, Field.Store.YES));
			}
			if (!Objects.isNull(doc.header)) {
				parsedDoc.add(new StringField("HEADER", doc.header, Field.Store.YES));
			}

			parsedArr.add(parsedDoc);
		}

		return parsedArr;
	}
}
