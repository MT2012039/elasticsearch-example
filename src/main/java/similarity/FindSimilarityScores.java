package similarity;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.E5SmallV2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.CosineSimilarity;
import document.Document;
import json.JsonParser;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FindSimilarityScores {

    public static void main(String[] args) throws IOException {

        EmbeddingModel embeddingModel = new E5SmallV2EmbeddingModel();
        String [] filterObjects = {"Account", "Case", "Lead", "Campaign","Opportunity"};
       List<String> filObjects = Arrays.asList(filterObjects);
       JsonParser jsonParser = new JsonParser();
       List<Document> mistralList = jsonParser.getDocuments("src/main/resources/summary/mistral/salesforce/withattributes")
               .stream().filter(document -> filObjects.contains(document.getName())).collect(Collectors.toList());
       List<Document> gpt4List = jsonParser.getDocuments("src/main/resources/summary/gpt4/salesforce/withattributes")
               .stream().filter(document -> filObjects.contains(document.getName())).collect(Collectors.toList());;
       List<Document> gpt35List = jsonParser.getDocuments("src/main/resources/summary/gpt35turbo/salesforce/withattributes")
               .stream().filter(document -> filObjects.contains(document.getName())).collect(Collectors.toList());;
       for (Document document : mistralList) {
           Embedding mistral_summary = embeddingModel.embed(document.getSummary()).content();
           Embedding description = embeddingModel.embed(document.getDescription()).content();
           Document gpt4doc = findDocument(gpt4List, document.getName());
           Embedding gpt4_summary = embeddingModel.embed(gpt4doc.getSummary()).content();
           Document gpt35doc = findDocument(gpt35List, document.getName());
           Embedding gpt35summary = embeddingModel.embed(gpt35doc.getSummary()).content();
           double mistral_desc = CosineSimilarity.between(mistral_summary, description);
           double mistral_gpt4 = CosineSimilarity.between(mistral_summary, gpt4_summary);
           double mistral_gpt35 = CosineSimilarity.between(mistral_summary, gpt35summary);
           double gpt4_desc = CosineSimilarity.between(gpt4_summary, description);
           double gpt35_desc = CosineSimilarity.between(gpt35summary, description);
           System.out.println("Object : " + document.getName());
           System.out.println("Description\n..................\n" + document.getDescription());
           System.out.println("Mistral Summary\n...............\n" + document.getSummary());
           System.out.println("gpt4 Summary\n...............\n" + gpt4doc.getSummary());
           System.out.println("gpt3.5 TURBO Summary\n...............\n" + gpt35doc.getSummary());
           System.out.println("Similarity Scores\n...................");
           System.out.println("Similarity with mistral_description : " + mistral_desc);
           System.out.println("Similarity with mistral and gpt4 : " + mistral_gpt4);
           System.out.println("Similarity with mistral and gpt35 : " + mistral_gpt35);
           System.out.println("Similarity with gpt4 and description : " + gpt4_desc);
           System.out.println("Similarity with gpt3.5 and description : " + gpt35_desc);


       }
    }

    public static Document findDocument(List<Document> documents, String docName) {
        return documents.stream().filter(document -> document.getName().equals(docName)).findFirst().get();
    }
}
