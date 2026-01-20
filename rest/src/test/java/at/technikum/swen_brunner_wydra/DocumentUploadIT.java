package at.technikum.swen_brunner_wydra;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DocumentUploadIT {

    private static final ObjectMapper OM = new ObjectMapper();

    // --- Containers ----------------------------------------------------------
    static final PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("swenprojekt")
                    .withUsername("postgres")
                    .withPassword("postgres");

    static final RabbitMQContainer rabbit =
            new RabbitMQContainer("rabbitmq:3.13-management");

    static final GenericContainer<?> minio =
            new GenericContainer<>("minio/minio:latest")
                    .withEnv("MINIO_ROOT_USER", "minioadmin")
                    .withEnv("MINIO_ROOT_PASSWORD", "minioadmin")
                    .withCommand("server /data --console-address :9001")
                    .withExposedPorts(9000);

    // Start containers BEFORE Spring reads DynamicPropertySource
    static {
        Startables.deepStart(Stream.of(postgres, rabbit, minio)).join();
    }

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        // DB
        r.add("spring.datasource.url", postgres::getJdbcUrl);
        r.add("spring.datasource.username", postgres::getUsername);
        r.add("spring.datasource.password", postgres::getPassword);

        // RabbitMQ
        r.add("spring.rabbitmq.host", rabbit::getHost);
        r.add("spring.rabbitmq.port", rabbit::getAmqpPort);
        r.add("spring.rabbitmq.username", () -> "guest");
        r.add("spring.rabbitmq.password", () -> "guest");

        // MinIO (hier beide Varianten, je nachdem wie ihr es in der App auslest)
        r.add("MINIO_ENDPOINT", () -> "http://" + minio.getHost() + ":" + minio.getMappedPort(9000));
        r.add("MINIO_ACCESS_KEY", () -> "minioadmin");
        r.add("MINIO_SECRET_KEY", () -> "minioadmin");
        r.add("MINIO_BUCKET", () -> "documents");

        // JWT + internal secret (WICHTIG: Keys so wie im JwtService!)
        r.add("app.jwt.secret", () -> "0123456789abcdef0123456789abcdef0123456789abcdef");
        r.add("app.jwt.expirationMinutes", () -> "1440");

        // Falls ihr das irgendwo als app.internal.secret erwartet:
        r.add("app.internal.secret", () -> "dev-secret-change-me");
    }

    @Autowired TestRestTemplate rest;
    @Autowired RabbitTemplate rabbitTemplate;

    private MinioClient minioClient;

    @BeforeAll
    void setupMinioBucket() throws Exception {
        String endpoint = "http://" + minio.getHost() + ":" + minio.getMappedPort(9000);

        minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials("minioadmin", "minioadmin")
                .build();

        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket("documents").build()
        );
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket("documents").build());
        }
    }

    @Test
    void documentUpload_shouldCreateDbEntry_storeFileInMinio_andPublishRabbitMessage() throws Exception {
        // 1) Register + Login -> JWT
        String email = "it_" + UUID.randomUUID() + "@example.com";
        String password = "pw123456";
        register(email, password);
        String token = loginAndGetToken(email, password);
        assertThat(token).isNotBlank();

        // 2) Upload PDF
        byte[] pdfBytes = minimalPdfBytes();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        ByteArrayResource file = new ByteArrayResource(pdfBytes) {
            @Override public String getFilename() { return "test.pdf"; }
        };

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", file);

        ResponseEntity<String> uploadRes = rest.postForEntity(
                "/api/upload",
                new HttpEntity<>(body, headers),
                String.class
        );

        assertThat(uploadRes.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(uploadRes.getBody()).isNotNull();

        Long docId = Long.valueOf(uploadRes.getBody().replaceAll("[^0-9]", ""));
        assertThat(docId).isPositive();

        // 3) Dokumentliste prüfen
        ResponseEntity<String> docsRes = rest.exchange(
                "/api/dokumente",
                HttpMethod.GET,
                new HttpEntity<>(authHeaders(token)),
                String.class
        );
        assertThat(docsRes.getStatusCode().is2xxSuccessful()).isTrue();

        JsonNode docs = OM.readTree(docsRes.getBody());
        assertThat(docs.isArray()).isTrue();

        JsonNode found = null;
        for (JsonNode d : docs) {
            if (d.hasNonNull("id") && d.get("id").asLong() == docId) {
                found = d;
                break;
            }
        }
        assertThat(found).as("Uploaded document should appear in /api/dokumente").isNotNull();

        String objectKey = found.hasNonNull("dateiname") ? found.get("dateiname").asText() : null;
        assertThat(objectKey).as("dateiname/objectKey must be present").isNotBlank();

        // 4) MinIO: Objekt existiert
        StatObjectResponse stat = minioClient.statObject(
                StatObjectArgs.builder().bucket("documents").object(objectKey).build()
        );
        assertThat(stat.size()).isGreaterThan(0);

        // 5) Rabbit: Message wurde gepublished (Queue-Name muss zu eurer App passen!)
        Object msg = rabbitTemplate.receiveAndConvert("ocr.incoming", Duration.ofSeconds(5).toMillis());
        assertThat(msg).as("Expected an upload message in ocr.incoming").isNotNull();

        String msgStr = msg.toString();
        assertThat(msgStr).contains(String.valueOf(docId));
        assertThat(msgStr).contains(objectKey);
    }

    // ---------- helpers ----------
    private void register(String email, String password) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);

        String json = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
        ResponseEntity<String> res = rest.postForEntity("/auth/register", new HttpEntity<>(json, h), String.class);

        assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);

        String json = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
        ResponseEntity<String> res = rest.postForEntity("/auth/login", new HttpEntity<>(json, h), String.class);

        assertThat(res.getStatusCode().is2xxSuccessful()).isTrue();

        JsonNode node = OM.readTree(res.getBody());
        assertThat(node.hasNonNull("token")).isTrue();
        return node.get("token").asText();
    }

    private HttpHeaders authHeaders(String token) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(token);
        return h;
    }

    private byte[] minimalPdfBytes() {
        String pdf =
                "%PDF-1.4\n" +
                        "1 0 obj<<>>endobj\n" +
                        "xref\n0 2\n0000000000 65535 f \n0000000010 00000 n \n" +
                        "trailer<<>>\nstartxref\n0\n%%EOF\n";
        return pdf.getBytes(StandardCharsets.US_ASCII);
    }
}
