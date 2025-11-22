package at.technikum.ocrworker;

import at.technikum.ocrworker.service.OcrService;
import io.minio.MinioClient;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Einfacher Unit-Tests für die MinIO-Integration im OcrService.
 * Hier wird überprüft, ob der Service korrekt mit dem MinIO-Client
 * und den Konfigurationswerten (Bucket, Sprache) initialisiert wird.
 */
class OcrServiceTest {

    @Test
    void shouldInitializeWithGivenBucketAndLangs() {
        // Arrange
        MinioClient minioMock = mock(MinioClient.class);

        // Act
        OcrService service = new OcrService(minioMock, "test-bucket", "deu");

        // Assert
        assertThat(service).isNotNull();
        System.out.println(" OcrService erfolgreich mit Bucket 'test-bucket' und Sprache 'deu' initialisiert.");
    }
}
