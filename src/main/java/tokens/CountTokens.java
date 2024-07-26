package tokens;

import com.fasterxml.jackson.databind.ObjectMapper;
import salesforce.Document;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import ai.djl.huggingface.tokenizers.Encoding;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;


public class CountTokens {

    String DJL_MODEL = "sentence-transformers/all-MiniLM-L6-v2";
    public void countTokens(List<Document> documentList) throws IOException {
        Map<String, Integer> countTokens = new HashMap<>();
        for(Document document : documentList) {
            String json = document.toString();
            String [] tokens = getLLMTokens(json);
            List<String> token_filtered = removeUnwantedTokens(tokens);
            countTokens.put(document.getName(), token_filtered.size());
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

    public String[] getLLMTokens(String text) {
        Encoding encoding = HuggingFaceTokenizer.newInstance(DJL_MODEL, getDJLConfig()).encode(text);
        return encoding.getTokens();
    }

    public List<String> removeUnwantedTokens(String [] tokens) {
        return Arrays.stream(tokens).filter(s -> !(s.equals("{") || s.equals("") || s.equals("}") || s.equals(":") || s.equals(" ") || s.equals("\"") || s.equals(","))).collect(Collectors.toList());
    }
}
