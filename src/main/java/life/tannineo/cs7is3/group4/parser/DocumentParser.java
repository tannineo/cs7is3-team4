package life.tannineo.cs7is3.group4.parser;

import org.apache.lucene.document.Document;

import java.util.ArrayList;

public interface DocumentParser<T> {

	ArrayList<T> readFile(String filePath) throws Exception;

	ArrayList<Document> toLucDoc(ArrayList<T> arr);
}
