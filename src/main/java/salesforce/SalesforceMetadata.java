package salesforce;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.force.api.*;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class SalesforceMetadata {

    ForceApi forceApi;
    public SalesforceMetadata() {
        ApiSession session = new ApiSession(AccessToken.AT, "https://informatica-7b-dev-ed.develop.my.salesforce.com");
        forceApi = new ForceApi(session);
    }

    public List<String> getAllObjects() {
        DescribeGlobal global = forceApi.describeGlobal();
        List<String> objects = new ArrayList<>();
        for (DescribeSObjectBasic basic : global.getSObjects()) {
            objects.add(basic.getName());
        }
        return objects;
    }

    public List<Document> getObjectMetadata(List<String> objects) throws InterruptedException {
        List<Document> documents = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Callable<DescribeSObject>> callableTasks = new ArrayList<>(objects.size());
        objects.forEach(object -> callableTasks.add(() -> forceApi.describeSObject(object)));
        List<Future<DescribeSObject>> futures = executor.invokeAll(callableTasks);
        futures.forEach(future -> {
            try {
                Document document = new Document();
                document.setName(future.get().getName());
                document.setAttributes(future.get().getAllFields().stream().map(field -> new Attribute(field.getName(), field.getType())).collect(Collectors.toList()));
                document.setDescription(getDescription(future.get().getName()));
                document.setSummary(document.getDescription());
                documents.add(document);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        executor.shutdown();
        return documents;
    }

    public String getDescription(String object) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("C:\\workspace\\langchain4j-examples\\elasticsearch-example\\src\\main\\resources\\salesforce.json");
        Map<String, Object> userData = objectMapper.readValue(
                file, new TypeReference<Map<String, Object>>() {
                });
        if(userData.get(object) != null) {
           Map objectMap = (Map) userData.get(object);
           return objectMap.get("Description").toString();
        } else {
            return null;
        }
    }


}
