package at.technikum.logbatch.repository;

import at.technikum.logbatch.entity.DocumentAccessStat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DocumentAccessStatRepository
        extends JpaRepository<DocumentAccessStat, Long> {

    Optional<DocumentAccessStat> findByDocumentIdAndAccessDate(
            Long documentId,
            LocalDate accessDate
    );
}
