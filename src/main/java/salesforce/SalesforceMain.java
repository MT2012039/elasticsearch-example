package salesforce;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class SalesforceMain {

    public static void main(String[] args) throws InterruptedException {
        SalesforceMetadata metadata = new SalesforceMetadata();
        List<String> objects = metadata.getAllObjects();
        List<Document> documents = metadata.getObjectMetadata(objects);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            for (Document document : documents) {
                String json = objectMapper.writeValueAsString(document);
                File file = new File("C:\\workspace\\langchain4j-examples\\elasticsearch-example\\src\\main\\resources\\metadata\\"+document.getName()+".json");
                file.createNewFile();
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(json);
                fileWriter.flush();
                fileWriter.close();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
