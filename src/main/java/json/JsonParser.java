package json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import salesforce.Document;
import tokens.CountTokens;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonParser {

    ObjectMapper objectMapper = new ObjectMapper();
    public List<File> getAllJsonFiles(String path) {
        File folder = new File(path);
        List<File> files = new ArrayList<File>();
        for (File file : folder.listFiles())
        {
            files.add(file);

        }
        return files;
    }

    public List<Document> getDocuments(String path) throws IOException {
        List<File> files = getAllJsonFiles(path);
        List<Document> documents = new ArrayList<Document>();
        for(File f : files) {
           Document document =  objectMapper.readValue(f, new TypeReference<Document>(){});
           documents.add(document);
        }
        return documents;
    }

    public static void main(String[] args) throws IOException {
        JsonParser parser = new JsonParser();
        List<Document> documents = parser.getDocuments("C:\\workspace\\langchain4j-examples\\elasticsearch-example\\src\\main\\resources\\metadata");
        CountTokens tokens = new CountTokens();
        tokens.countTokens(documents);
        documents.forEach(doc -> System.out.println(doc.getName()));

    }
}
