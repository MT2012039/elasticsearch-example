

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.DocumentTransformer;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.*;
import dev.langchain4j.model.embedding.bge.small.en.v15.BgeSmallEnV15EmbeddingModel;
import dev.langchain4j.model.embedding.bge.small.en.v15.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.comparison.IsEqualTo;
import json.JsonParser;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import prompts.Prompts;
import salesforce.SalesforceMetadata;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import dev.langchain4j.data.document.Document;
import splitter.SplitDocuments;

public class ElasticsearchEmbeddingStoreExample {

    public static final Integer SPLIT_AT = 10000;
    public static final Integer MAX_OVERLAP = 100;
    public static final String  SPLIT_TYPE = "recursive";

    public static void main(String[] args) throws InterruptedException, IOException {
        HuggingFaceTokenizer tokenizer = new HuggingFaceTokenizer();
        SplitDocuments splitter = new SplitDocuments(SPLIT_TYPE, SPLIT_AT, MAX_OVERLAP);
        JsonParser parser = new JsonParser();
        List<TextSegment> salesforce_segments = new ArrayList<>();
        List<TextSegment> servicenow_segments = new ArrayList<>();
        List<Document> documents = FileSystemDocumentLoader.loadDocuments("src/main/resources/metadata/salesforce");
        for (Document document: documents) {
            if (tokenizer.estimateTokenCountInText(document.text()) > SPLIT_AT) {
                salesforce_segments.addAll(splitter.getSplits(document));
            } else {
                salesforce_segments.add(document.toTextSegment());
            }

        }
        documents = FileSystemDocumentLoader.loadDocuments("src/main/resources/metadata/servicenow");
        for (Document document: documents) {
            if (tokenizer.estimateTokenCountInText(document.text()) > SPLIT_AT) {
                servicenow_segments.addAll(splitter.getSplits(document));
            } else {
                servicenow_segments.add(document.toTextSegment());
            }

        }

        try (ElasticsearchContainer elastic = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.9.0")
                .withEnv("xpack.security.enabled", "false")) {
            elastic.start();

            EmbeddingStore<TextSegment> embeddingStore = ElasticsearchEmbeddingStore.builder()
                    .serverUrl("http://" + elastic.getHttpHostAddress())
                    .dimension(384)
                    .indexName("metadata-rag1")
                    .build();

            //Change the model here for experiment
            EmbeddingModel embeddingModel = new E5SmallV2EmbeddingModel();

            for (TextSegment textSegment : salesforce_segments) {
                textSegment.metadata().put("adapter", "Salesforce");
                Embedding embedding1 = embeddingModel.embed(textSegment).content();
                embeddingStore.add(embedding1, textSegment);
            }

            for (TextSegment textSegment : servicenow_segments) {
                textSegment.metadata().put("adapter", "ServiceNow");
                Embedding embedding1 = embeddingModel.embed(textSegment).content();
                embeddingStore.add(embedding1, textSegment);
            }


            Thread.sleep(1000); // to be sure that embeddings were persisted
            Set<String> propmtset = Prompts.app_to_app_prompts.keySet();
            for (String promptKey : propmtset) {
                String prompt = Prompts.app_to_app_prompts.get(promptKey);
                System.out.println("Prompt: " + prompt);
                Embedding queryEmbedding = embeddingModel.embed(prompt).content();
                EmbeddingSearchRequest request = new EmbeddingSearchRequest(queryEmbedding, 5, 0.91, null);
                List<EmbeddingMatch<TextSegment>> relevant_returns = embeddingStore.search(request).matches();
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
