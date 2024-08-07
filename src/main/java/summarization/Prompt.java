package summarization;

import document.Document;

public class Prompt {

    public static String getPrompt(Document document, boolean withAttributes) {

        if (withAttributes) {
            return String.format("Summarize the object {%s} for application {%s} with attributes {%s}", document.getName(), document.getApplication(), document.getAttributesString());
        } else {
            return String.format("Summarize the object {%s} for application {%s} ", document.getName(), document.getApplication());
        }
    }
}
