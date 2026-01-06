package at.technikum.swen_brunner_wydra.repository;

import at.technikum.swen_brunner_wydra.entity.Dokument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DokumentRepository extends JpaRepository<Dokument, Long> {

    // Alle Dokumente eines Users
    List<Dokument> findAllByUserId(Long userId);

    // Ein Dokument nur laden, wenn es dem User gehört
    Optional<Dokument> findByIdAndUserId(Long id, Long userId);

    // Löschen nur, wenn es dem User gehört
    long deleteByIdAndUserId(Long id, Long userId);
}
