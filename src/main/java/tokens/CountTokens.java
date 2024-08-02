package tokens;

import com.fasterxml.jackson.databind.ObjectMapper;
import document.Document;
import java.io.File;
import java.io.IOException;
import java.util.*;


public class CountTokens {

    public void countTokens(List<Document> documentList) throws IOException {
        Map<String, Integer> countTokens = new HashMap<>();
        for(Document document : documentList) {
            int token_count = getTokenCount(document.toString());
            countTokens.put(document.getName(), token_count);
        }
        Map<String, Integer> sorted = sortByValue(countTokens);
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File("C:\\workspace\\langchain4j-examples\\elasticsearch-example\\src\\main\\resources\\tokens\\tokens.json"), sorted);
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    public Map<String, String> getDJLConfig() {
        Map<String, String> options = new HashMap<String, String>();
        options.put("addSpecialTokens", "false");
        options.put("padding", "false");
        options.put("modelMaxLength", "100000");
        options.put("maxLength", "100000");
        return options;
    }

    public int getTokenCount(String text) {
        dev.langchain4j.model.embedding.HuggingFaceTokenizer tokenizer = new dev.langchain4j.model.embedding.HuggingFaceTokenizer();
        return tokenizer.estimateTokenCountInText(text);
    }
}
