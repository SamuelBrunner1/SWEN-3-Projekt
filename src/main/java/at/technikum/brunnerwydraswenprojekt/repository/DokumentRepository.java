package at.technikum.brunnerwydraswenprojekt.repository;

import at.technikum.brunnerwydraswenprojekt.entity.Dokument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DokumentRepository extends JpaRepository<Dokument, Long> {
}
