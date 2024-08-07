package query.salesforce;

import elastic.ElasticsearchEmbeddingStoreExample;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QueryPrompts {

    ElasticsearchEmbeddingStoreExample elastic;

    public QueryPrompts() throws IOException, InterruptedException {
        elastic = new ElasticsearchEmbeddingStoreExample();
    }

    public static final String prompt = "You are a Salesforce expert. Please help to generate SOQL(Structured Object Query Language)to answer the question.\\n\" +\n" +
            "                \"\\n\" +\n" +
            "                \"Your response should ONLY be based on the given context and follow the response guidelines and format instructions.\\n\" +\n" +
            "                \"\\n\" +\n" +
            "                \"Use the input: {query_input} as the input for the query. If no value is passed in input then ignore it.\\n\" +\n" +
            "                \"\\n\" +\n" +
            "                \"===Below is the Salesforce Schema\\n\" +\n" +
            "                \"\\n\" +\n" +
            "                {%s}    + \n"+
            "                \"\\n\" +\n" +
            "                \"\\n\" +\n" +
            "                \"\\n\" +\n" +
            "                \"===Response Guidelines\\n\" +\n" +
            "                \"\\n\" +\n" +
            "                \"Generate a valid SOQL for the given question.\\n\" +\n" +
            "                \"Only generate the query nothing else and the content without \\\\n\\n\" +\n" +
            "                \"Please use the most relevant table(s).\\n\" +\n" +
            "                \"\\n\" +\n" +
            "                \"\\n\" +\n" +
            "                \"===Question\\n\" +\n" +
            "                \"\\n\" +\n" +
            "                \"%s";

    public static final List<String> questions = Arrays.asList(new String[]{"Retrieve the stage of Salesforce accounts related to the Chevron Deal",
            "Retrieve details of Opportunities with ClosedWon stage for the currentmonth"
            , "Get Cases with their case origins and case reasons from Salesforce",
            "Fetch the top 10 Salesforce Opportunities by the expected close date filtered by a particular opportunity type",
            "Get Salesforce accounts whose name contains United."});


    public String getPrompt(String question) throws IOException, InterruptedException {
            String attributes = elastic.getResultForPrompt(question, "Salesforce", 2).stream().map(match ->
                    match.embedded().metadata().getString("name") + ":" +
                            match.embedded().metadata().getString("attributes")).collect(Collectors.joining("\n"));
            return String.format(prompt,attributes,question);
    }

}
