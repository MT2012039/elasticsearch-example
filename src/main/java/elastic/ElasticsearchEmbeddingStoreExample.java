package elastic;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.*;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.elasticsearch.ElasticsearchEmbeddingStore;
import dev.langchain4j.store.embedding.filter.comparison.IsEqualTo;
import json.JsonParser;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import prompts.Prompts;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import dev.langchain4j.data.document.Document;
import splitter.SplitDocuments;

public class ElasticsearchEmbeddingStoreExample {

    public static final Integer SPLIT_AT = 1000;
    public static final Integer MAX_OVERLAP = 200;
    public static final String  SPLIT_TYPE = "word";

    EmbeddingModel embeddingModel = new BgeSmallEnEmbeddingModel();
    EmbeddingStore<TextSegment> embeddingStore;
    ElasticsearchContainer elastic;
    public ElasticsearchEmbeddingStoreExample() throws IOException, InterruptedException {
        embedAll();
    }

    public void embedAll() throws InterruptedException, IOException {
        HuggingFaceTokenizer tokenizer = new HuggingFaceTokenizer();
        SplitDocuments splitter = new SplitDocuments(SPLIT_TYPE, SPLIT_AT, MAX_OVERLAP);
        JsonParser parser = new JsonParser();
        List<TextSegment> segements = new ArrayList<>();
        //Generate embeddings with only summary
        List<Document> documents = parser.getDocuments("src/main/resources/summary/gpt4/salesforce").stream().
                map(document -> new Document(document.getSummary(), new Metadata().put("name", document.getName()).put("adapter", "Salesforce")
                        .put("attributes", document.getAttributesString()))).collect(Collectors.toList());

        //Below code is to get from document as json
        //List<Document> documents = FileSystemDocumentLoader.loadDocuments("src/main/resources/summary/gpt35turbo/salesforce");

        documents.addAll(parser.getDocuments("src/main/resources/summary/gpt4/servicenow").stream().
                map(document -> new Document(document.getSummary(), new Metadata().put("name", document.getName()).
                        put("adapter", "ServiceNow").put("attributes", document.getAttributesString()))).collect(Collectors.toList()));

        //Below code is to get from document as json
        // documents.addAll(FileSystemDocumentLoader.loadDocuments("src/main/resources/summary/gpt35turbo/servicenow"));

        for (Document document : documents) {
            if (tokenizer.estimateTokenCountInText(document.text()) > SPLIT_AT) {
                segements.addAll(splitter.getSplits(document));
            } else {
                segements.add(document.toTextSegment());
            }

        }

        try {
            elastic = new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.9.0")
                .withEnv("xpack.security.enabled", "false") ;
            elastic.start();
            embeddingStore = ElasticsearchEmbeddingStore.builder()
                    .serverUrl("http://" + elastic.getHttpHostAddress())
                    .dimension(384)
                    .indexName("metadata-rag1")
                    .build();


            for (TextSegment textSegment : segements) {
                Embedding embedding1 = embeddingModel.embed(textSegment).content();
                embeddingStore.add(embedding1, textSegment);
            }
        } catch (Exception exception) {
            throw exception;
        }
        Thread.sleep(1000);
    }

    public void getResultsForPrompts() {
        embeddingStore = ElasticsearchEmbeddingStore.builder()
                .serverUrl("http://" + elastic.getHttpHostAddress())
                .dimension(384)
                .indexName("metadata-rag1")
                .build();
        Set<String> propmtset = Prompts.app_to_app_prompts.keySet();
        for (String promptKey : propmtset) {
            String prompt = Prompts.app_to_app_prompts.get(promptKey);
            System.out.println("Prompt: " + prompt);
            Embedding queryEmbedding = embeddingModel.embed(prompt).content();
            EmbeddingSearchRequest request = new EmbeddingSearchRequest(queryEmbedding, 5, 0.91, new IsEqualTo("adapter", "Salesforce"));
            List<EmbeddingMatch<TextSegment>> relevant_returns = embeddingStore.search(request).matches();
            for (int i=0;i<relevant_returns.size();i++) {
                EmbeddingMatch<TextSegment> embeddingMatch = relevant_returns.get(i);
                System.out.println(embeddingMatch.score());
                System.out.println(embeddingMatch.embedded().metadata().getString("name"));
                System.out.println(embeddingMatch.embedded().metadata().getString("attributes"));

            }

            System.out.println("-------------------------------------------------------------------------------");
        }
    }

    public List<EmbeddingMatch<TextSegment>> getResultForPrompt(String prompt,String application, int maxResults) {
        embeddingStore = ElasticsearchEmbeddingStore.builder()
                .serverUrl("http://" + elastic.getHttpHostAddress())
                .dimension(384)
                .indexName("metadata-rag1")
                .build();
        Embedding queryEmbedding = embeddingModel.embed(prompt).content();
        EmbeddingSearchRequest request = new EmbeddingSearchRequest(queryEmbedding, maxResults, 0.90, new IsEqualTo("adapter", application));
        List<EmbeddingMatch<TextSegment>> relevant_returns = embeddingStore.search(request).matches();
        return relevant_returns;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        ElasticsearchEmbeddingStoreExample elasticsearchEmbeddingStoreExample = new ElasticsearchEmbeddingStoreExample();
        elasticsearchEmbeddingStoreExample.getResultsForPrompts();
    }


}
