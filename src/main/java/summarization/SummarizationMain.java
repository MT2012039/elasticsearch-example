package summarization;

import document.Document;
import json.JsonParser;
import summarization.mistral.Mistral;
import summarization.openai.GPT35;
import summarization.openai.GPT4O;

import java.io.IOException;
import java.util.List;

public class SummarizationMain {

    //Change model here
    public static final String MODEL = "MISTRAL";

    public static void main(String[] args) throws IOException {
        JsonParser parser = new JsonParser();
        List<Document> documentList = parser.getDocuments("src/main/resources/metadata/servicenow");
        ModelSummary modelSummary = getModelClass();
        for (Document document : documentList) {
            String prompt = Prompt.getPrompt(document, false);
            modelSummary.getAndSaveSummary(prompt, document, "servicenow");
        }
    }

    public static ModelSummary getModelClass () {
        if (MODEL.equals("MISTRAL")) {
            return new Mistral();
        } else if (MODEL.equals("gpt4")) {
            return new GPT4O();
        } else if (MODEL.equals("gpt3.5turbo")) {
            return new GPT35();
        }
        return null;
    }

}
