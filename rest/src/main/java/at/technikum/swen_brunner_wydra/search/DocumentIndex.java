package at.technikum.swen_brunner_wydra.search;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "documents")
public class DocumentIndex {

    @Id
    private String id; // "70"
    private Long documentId;
    private String text;

    public String getId() { return id; }
    public Long getDocumentId() { return documentId; }
    public String getText() { return text; }
}
