

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class ConvertToJson {

    public static String convertMapToJson(Map map) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String json = objectMapper.writeValueAsString(map);
            return json;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
      return null;
    }
}
