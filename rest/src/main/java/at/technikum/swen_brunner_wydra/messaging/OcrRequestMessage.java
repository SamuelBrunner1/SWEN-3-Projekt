package at.technikum.swen_brunner_wydra.messaging;

public class OcrRequestMessage {

    private Long documentId;
    private String objectKey; // = dateiname / MinIO-Key

    public OcrRequestMessage() {
    }

    public OcrRequestMessage(Long documentId, String objectKey) {
        this.documentId = documentId;
        this.objectKey = objectKey;
    }

    public Long getDocumentId() { return documentId; }
    public void setDocumentId(Long documentId) { this.documentId = documentId; }

    public String getObjectKey() { return objectKey; }
    public void setObjectKey(String objectKey) { this.objectKey = objectKey; }
}