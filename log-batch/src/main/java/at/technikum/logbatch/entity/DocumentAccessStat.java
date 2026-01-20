package at.technikum.logbatch.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(
        name = "document_access_stats",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"document_id", "access_date"})
        }
)
public class DocumentAccessStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "access_date", nullable = false)
    private LocalDate accessDate;

    @Column(name = "access_count", nullable = false)
    private int accessCount;

    protected DocumentAccessStat() {
        // JPA
    }

    public DocumentAccessStat(Long documentId, LocalDate accessDate, int accessCount) {
        this.documentId = documentId;
        this.accessDate = accessDate;
        this.accessCount = accessCount;
    }

    public Long getId() {
        return id;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public LocalDate getAccessDate() {
        return accessDate;
    }

    public int getAccessCount() {
        return accessCount;
    }

    public void increaseCount(int delta) {
        this.accessCount += delta;
    }
}
