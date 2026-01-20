/*package at.technikum.swen_brunner_wydra;

import at.technikum.swen_brunner_wydra.entity.Dokument;
import at.technikum.swen_brunner_wydra.repository.DokumentRepository;
import at.technikum.swen_brunner_wydra.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        // ---- In-Memory DB (kein Docker nötig) -----------------------------
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",

        // ---- JWT (damit JwtService/JwtFilter nicht crasht) -----------------
        "app.jwt.secret=test-secret-1234567890",
        "app.jwt.expirationminutes=60",

        // ---- Internal API Secret ------------------------------------------
        "app.internal.secret=test-internal-secret",

        // ---- RabbitMQ deaktivieren (keine externen Verbindungen im Test) --
        "spring.rabbitmq.listener.simple.auto-startup=false",
        "spring.rabbitmq.listener.direct.auto-startup=false"
})
class SwenBrunnerWydraApplicationTests {

    @Autowired
    private DokumentRepository dokumentRepository;

    @Test
    void contextLoads() {
        // Spring Context startet vollständig
        assertThat(dokumentRepository).isNotNull();
    }

    @Test
    void entityCanBeCreated() {
        Dokument d = new Dokument();
        d.setTitel("Test Dokument");
        d.setInhalt("Inhalt");

        assertThat(d.getTitel()).isEqualTo("Test Dokument");
        assertThat(d.getInhalt()).isEqualTo("Inhalt");
    }

    @Test
    void repositoryCrudWithH2() {
        // Minimaler User (wegen @ManyToOne(nullable = false))
        User user = new User();
        user.setEmail("test@test.at");
        user.setPasswordHash("pw");

        Dokument doc = new Dokument();
        doc.setTitel("Persist Test");
        doc.setInhalt("Hello DB");
        doc.setUser(user);

        Dokument saved = dokumentRepository.save(doc);

        assertThat(saved.getId()).isNotNull();
        assertThat(
                dokumentRepository.findById(saved.getId())
        ).isPresent()
                .get()
                .extracting(Dokument::getTitel)
                .isEqualTo("Persist Test");
    }
}
*/