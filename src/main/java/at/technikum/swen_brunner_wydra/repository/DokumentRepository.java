package at.technikum.swen_brunner_wydra.repository;

import at.technikum.swen_brunner_wydra.entity.Dokument;  //
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DokumentRepository extends JpaRepository<Dokument, Long> { //DB Zugang
}
