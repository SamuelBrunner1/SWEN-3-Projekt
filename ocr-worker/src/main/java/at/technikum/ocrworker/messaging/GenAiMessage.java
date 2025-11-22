package at.technikum.ocrworker.messaging;

public class GenAiMessage {

    private Long documentId;
    private String ocrText;

    public GenAiMessage() {
    }

    public GenAiMessage(Long documentId, String ocrText) {
        this.documentId = documentId;
        this.ocrText = ocrText;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    public String getOcrText() {
        return ocrText;
    }

    public void setOcrText(String ocrText) {
        this.ocrText = ocrText;
    }
}
