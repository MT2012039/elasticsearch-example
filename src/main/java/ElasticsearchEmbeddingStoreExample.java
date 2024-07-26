

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.*;
import dev.langchain4j.model.embedding.bge.small.en.v15.BgeSmallEnV15EmbeddingModel;
import dev.langchain4j.model.embedding.bge.small.en.v15.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import json.JsonParser;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import prompts.Prompts;
import salesforce.Document;
import salesforce.SalesforceMetadata;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class ElasticsearchEmbeddingStoreExample {

    public static void main(String[] args) throws InterruptedException, IOException {

        JsonParser parser = new JsonParser();
        List<Document> documents = parser.getDocuments("C:\\workspace\\langchain4j-examples\\elasticsearch-example\\src\\main\\resources\\metadata");
        ObjectMapper mapper = new ObjectMapper();
        try (ElasticsearchContainer elastic = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.9.0")
                .withEnv("xpack.security.enabled", "false")) {
            elastic.start();

            EmbeddingStore<TextSegment> embeddingStore = ElasticsearchEmbeddingStore.builder()
                    .serverUrl("http://" + elastic.getHttpHostAddress())
                    .dimension(384)
                    .indexName("metadata-rag")
                    .build();

            //Change the model here for experiment

            EmbeddingModel embeddingModel = new BgeSmallEnV15EmbeddingModel();

            for (Document document: documents) {
                TextSegment segment1 = TextSegment.from(document.toString());
                Embedding embedding1 = embeddingModel.embed(segment1).content();
                embeddingStore.add(embedding1, segment1);
            }

            Thread.sleep(1000); // to be sure that embeddings were persisted
            Set<String> propmtset = Prompts.promptMap.keySet();
            for (String promptKey : propmtset) {
                String prompt = Prompts.promptMap.get(promptKey);
                Embedding queryEmbedding = embeddingModel.embed(prompt).content();
                List<EmbeddingMatch<TextSegment>> relevant_returns = embeddingStore.findRelevant(queryEmbedding, 5);
                for (int i=0;i<relevant_returns.size();i++) {
                    EmbeddingMatch<TextSegment> embeddingMatch = relevant_returns.get(i);
                    System.out.println(embeddingMatch.score());
                    System.out.println(embeddingMatch.embedded().text());

                }

                System.out.println("-------------------------------------------------------------------------------");
            }
        }
    }
}
