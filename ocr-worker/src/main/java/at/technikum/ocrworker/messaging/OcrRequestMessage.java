package at.technikum.ocrworker.messaging;

public class OcrRequestMessage {

    private Long documentId;
    private String objectKey;

    public OcrRequestMessage() {
    }

    public Long getDocumentId() { return documentId; }
    public void setDocumentId(Long documentId) { this.documentId = documentId; }

    public String getObjectKey() { return objectKey; }
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }
}