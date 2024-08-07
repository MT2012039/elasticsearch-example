package query;

import query.salesforce.QueryPrompts;
import query.salesforce.validator.SyntaxValidator;
import summarization.mistral.Mistral;
import summarization.openai.GPT35;
import summarization.openai.GPT4O;

import java.io.IOException;

public class QueryMain {

    public static final String MODEL = "gpt4";

    public static void main(String[] args) throws IOException, InterruptedException {
        QuerySummary summary = getModelClass();
        QueryPrompts queryPrompts = new QueryPrompts();
        SyntaxValidator validator = new SyntaxValidator();
        for (String question : QueryPrompts.questions) {
            String prompt = queryPrompts.getPrompt(question);
            String query = summary.getAndSaveQuery(prompt, "salesforce");
            System.out.println("Original prompt : " + question);
            System.out.println("Generated prompt for query : \n");
            System.out.println(prompt);
            System.out.println("\n");
            System.out.println("Generated Query : " + query);
            boolean isValid = validator.isSyntaxValid(query);
            System.out.println("Is query syntax valid:" + isValid);
        }
    }

    public static QuerySummary getModelClass () {
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
