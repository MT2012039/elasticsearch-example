package splitter;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.*;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.HuggingFaceTokenizer;
import java.util.List;

public class SplitDocuments {

    DocumentSplitter splitter;
    public SplitDocuments(String type, int maxTokenSize, int maxOverlap) {
        splitter = getSplitter(type, maxTokenSize, maxOverlap);
    }
    public DocumentSplitter getSplitter(String type,int maxTokenSize,int maxOverlap) {
        if (type.equals("word")) {
            return new DocumentByWordSplitter(maxTokenSize, maxOverlap, new HuggingFaceTokenizer());
        } else if (type.equals("sentence")) {
            return new DocumentBySentenceSplitter(maxTokenSize, maxOverlap, new HuggingFaceTokenizer());
        } else if (type.equals("paragraph")) {
            return new DocumentByParagraphSplitter(maxTokenSize, maxOverlap, new HuggingFaceTokenizer());
        } else if (type.equals("line")) {
            return new DocumentByLineSplitter(maxTokenSize, maxOverlap, new HuggingFaceTokenizer());
        } else if(type.equals("recursive")) {
            return DocumentSplitters.recursive(maxTokenSize, maxOverlap, new HuggingFaceTokenizer());
        }
        return new DocumentByWordSplitter(maxTokenSize, maxOverlap, new HuggingFaceTokenizer());
    }

    public List<TextSegment> getSplits(Document document) {
        return splitter.split(document);
    }

}
