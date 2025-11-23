package at.technikum.ocrworker.messaging;

import java.io.Serializable;

public class GenAiMessage implements Serializable {

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

    @Override
    public String toString() {
        return "GenAiMessage{" +
                "documentId=" + documentId +
                ", ocrText='" + (ocrText != null
                ? ocrText.substring(0, Math.min(50, ocrText.length())) + "..."
                : null) + "'" +
                '}';
    }
}
