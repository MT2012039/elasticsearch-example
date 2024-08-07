package json;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import document.Document;
import tokens.CountTokens;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonParser {

    ObjectMapper objectMapper = new ObjectMapper();

    public List<File> getAllJsonFiles(String path) {
        File folder = new File(path);
        List<File> files = new ArrayList<File>();
        for (File file : folder.listFiles())
        {
            if(file.isFile()) {
                files.add(file);
            }

        }
        return files;
    }

    public List<Document> getDocuments(String path) throws IOException {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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
