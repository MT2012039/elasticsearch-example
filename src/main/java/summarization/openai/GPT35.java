package summarization.openai;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import document.Document;
import query.QuerySummary;
import summarization.ApiKey;
import summarization.ModelSummary;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_3_5_TURBO;

public class GPT35 implements ModelSummary, QuerySummary {

    ObjectMapper objectMapper = new ObjectMapper();
    ChatLanguageModel model;
    public GPT35() {
        model = OpenAiChatModel.builder()
                .apiKey(ApiKey.OPEN_AI_API_KEY) // Please use your own OpenAI API key
                .modelName(GPT_3_5_TURBO)
                .build();
    }
    @Override
    public void getAndSaveSummary(String prompt, Document document,String application) throws IOException {
        String response = model.generate(prompt);
        document.setSummary(response);
        String json = objectMapper.writeValueAsString(document);
        File file = new File("src/main/resources/summary/gpt35turbo/"+application+"/"+document.getName()+".json");
        file.createNewFile();
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(json);
        fileWriter.flush();
        fileWriter.close();
    }

    @Override
    public String getAndSaveQuery(String prompt, String application) {
        String response = model.generate(prompt);
        return response;
    }




}
