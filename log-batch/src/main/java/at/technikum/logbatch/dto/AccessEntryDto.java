package at.technikum.logbatch.dto;

import jakarta.xml.bind.annotation.XmlElement;

public class AccessEntryDto {

    private Long documentId;
    private int count;

    @XmlElement(name = "documentId")
    public Long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Long documentId) {
        this.documentId = documentId;
    }

    @XmlElement(name = "count")
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
