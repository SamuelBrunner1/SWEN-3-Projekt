package at.technikum.swen_brunner_wydra.elasticworker.elasticsearch;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.Instant;

@Document(indexName = "documents")
public class DocumentIndex {

    @Id
    private String id;          // = documentId als String (stabil / eindeutig)
    private Long documentId;
    private String text;
    private Instant indexedAtUtc;

    public DocumentIndex() {}

    public DocumentIndex(String id, Long documentId, String text, Instant indexedAtUtc) {
        this.id = id;
        this.documentId = documentId;
        this.text = text;
        this.indexedAtUtc = indexedAtUtc;
    }

    public String getId() { return id; }
    public Long getDocumentId() { return documentId; }
    public String getText() { return text; }
    public Instant getIndexedAtUtc() { return indexedAtUtc; }
}
