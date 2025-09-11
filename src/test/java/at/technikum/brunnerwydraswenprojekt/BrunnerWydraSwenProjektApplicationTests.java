package at.technikum.brunnerwydraswenprojekt;

import at.technikum.brunnerwydraswenprojekt.entity.Dokument;
import at.technikum.brunnerwydraswenprojekt.repository.DokumentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        // H2-In-Memory DB für Tests -> keine Abhängigkeit zu Postgres
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class BrunnerWydraSwenProjektApplicationTests {

    @Autowired
    private DokumentRepository dokumentRepository;

    @Test
    void contextLoads() {
        // Spring-Context startet erfolgreich (Repository wird injiziert)
        assertThat(dokumentRepository).isNotNull();
    }

    @Test
    void entityCanBeCreated() {
        // Dokument-Objekt lässt sich mit Titel/Inhalt erzeugen
        Dokument d = new Dokument();
        d.setTitel("Test Dokument");
        d.setInhalt("Inhalt");
        assertThat(d.getTitel()).isEqualTo("Test Dokument");
        assertThat(d.getInhalt()).isEqualTo("Inhalt");
    }

    @Test
    void repositoryCrudWithH2() {
        // Repository speichert & liest in H2 (ohne echte DB)
        Dokument in = new Dokument();
        in.setTitel("Persist Test");
        in.setInhalt("Hello DB");
        Dokument saved = dokumentRepository.save(in);

        assertThat(saved.getId()).isNotNull();
        assertThat(dokumentRepository.findById(saved.getId()))
                .isPresent()
                .get()
                .extracting(Dokument::getTitel)
                .isEqualTo("Persist Test");
    }
}
