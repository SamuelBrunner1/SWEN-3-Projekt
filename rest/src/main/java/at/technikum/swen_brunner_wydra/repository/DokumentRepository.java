package at.technikum.swen_brunner_wydra.repository;

import at.technikum.swen_brunner_wydra.entity.Dokument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DokumentRepository extends JpaRepository<Dokument, Long> {

    List<Dokument> findAllByUser_Id(Long userId);

    Optional<Dokument> findByIdAndUser_Id(Long id, Long userId);

    boolean existsByIdAndUser_Id(Long id, Long userId);

    void deleteByIdAndUser_Id(Long id, Long userId);
}
