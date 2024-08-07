package summarization;

import document.Document;

import java.io.IOException;

public interface ModelSummary {

    public void getAndSaveSummary(String prompt, Document document, String application) throws IOException;
}
