package at.technikum.ocrworker;

import org.junit.jupiter.api.Test;
import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integrationstest für Tesseract:
 * Führt eine echte OCR-Erkennung auf einem Testbild durch
 * und prüft, ob überhaupt Text erkannt wird.
 */
class OcrWorkerIntegrationTest {

    @Test
    void tesseractCanReadSimpleImage() throws Exception {
        File inputImage = new File("src/test/resources/test-image.png");
        if (!inputImage.exists()) {
            System.out.println(" Testbild nicht gefunden — Test übersprungen (" + inputImage.getAbsolutePath() + ")");
            return;
        }

        // Pfad zu Tesseract (direkter Aufruf, funktioniert auch ohne PATH)
        String tesseractPath = "C:\\Program Files\\Tesseract-OCR\\tesseract.exe";

        // Tesseract soll Ausgabe in temporäre Datei schreiben
        File outputBase = File.createTempFile("ocr_test_output", "");
        outputBase.delete(); // Tesseract hängt .txt automatisch an

        ProcessBuilder pb = new ProcessBuilder(
                tesseractPath,
                inputImage.getAbsolutePath(),
                outputBase.getAbsolutePath()
        );
        pb.redirectErrorStream(true);
        Process process = pb.start();
        int exitCode = process.waitFor();

        // Prüfung: Tesseract konnte das Bild verarbeiten
        assertThat(exitCode)
                .as("Tesseract sollte das Bild erfolgreich verarbeiten")
                .isEqualTo(0);

        // Output lesen
        File outputTextFile = new File(outputBase.getAbsolutePath() + ".txt");
        String recognizedText = new String(java.nio.file.Files.readAllBytes(outputTextFile.toPath()));

        System.out.println(" Erkannter Text:\n" + recognizedText);

        // Prüfung: irgendein Text wurde erkannt
        assertThat(recognizedText.trim())
                .as("Tesseract sollte irgendeinen Text erkennen")
                .isNotEmpty();
    }
}
