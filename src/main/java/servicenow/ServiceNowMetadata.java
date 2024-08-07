package servicenow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.force.api.ApiSession;
import com.force.api.ForceApi;
import com.force.api.http.Http;
import com.force.api.http.HttpRequest;
import com.force.api.http.HttpResponse;
import document.Document;
import salesforce.Attribute;
import salesforce.SalesforceMetadata;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ServiceNowMetadata {

    ObjectMapper mapper = new ObjectMapper();
    File table = new File("src/main/resources/servicenow/sys_db_object.json");
    File desc = new File("src/main/resources/servicenow/servicenow.json");
    public ServiceNowMetadata () {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<Document> getAllObjects() throws IOException {
        Map<String, Description> desc_map = mapper.readValue(desc, new TypeReference<Map<String, Description>>(){});
        Map<String,List<Result>> m = mapper.readValue(table,  new TypeReference<Map<String,List<Result>>>(){});
        List<Result> results = m.get("result");
        List<Document> documents = new ArrayList<>();
        for (Result result : results) {
           if (desc_map.get(result.getLabel()) != null) {
               Document document = new Document();
               document.setName(result.getName());
               document.setLabel(result.getLabel());
               document.setDescription(desc_map.get(result.getLabel()).getDescription());
               document.setApplication("servicenow");
               documents.add(document);
           }
        }
       return documents;
    }

    public void getMetadata(List<Document> documents) throws IOException {
        for (Document document : documents) {
            String url = "https://dev269544.service-now.com/api/now/doc/table/schema/"+document.getName();
            HttpRequest request = new HttpRequest().url(url).method("GET").header("Accept", "application/json").expectsCode(200).responseFormat(HttpRequest.ResponseFormat.STRING);
            request.setAuthorization("Bearer " + AccessToken.AT);
            HttpResponse response = Http.send(request);
            Map<String, List<AttributeResult>> m;
            try {

                m = mapper.readValue(response.getString(), new TypeReference<Map<String, List<AttributeResult>>>() {
                });
            } catch (Exception exception) {
                continue;
            }
            List<AttributeResult> attributeResults = m.get("result");
            List<Attribute> attributes = new ArrayList<>();
            for (AttributeResult attributeResult : attributeResults) {
                Attribute attribute = new Attribute(attributeResult.getName(), attributeResult.internalType);
                attributes.add(attribute);
            }
            document.setAttributes(attributes);
            String json = mapper.writeValueAsString(document);
            File file = new File("src/main/resources/metadata/servicenow/"+document.getName()+".json");
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(json);
            fileWriter.flush();
            fileWriter.close();
        }

    }

    public static void main(String[] args) throws Exception {

        ServiceNowMetadata metadata = new ServiceNowMetadata();
        List<Document> documents = metadata.getAllObjects();
        metadata.getMetadata(documents);
    }
}
