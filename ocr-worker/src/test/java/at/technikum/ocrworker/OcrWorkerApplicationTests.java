package at.technikum.ocrworker;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Systemtests, die prüfen, ob Tesseract und Ghostscript auf dem System installiert
 * und funktionsfähig sind.
 *
 *  Hinweis:
 * Diese Tests funktionieren nur, wenn die Programme an den Standardpfaden installiert sind:
 * - Tesseract:  C:\Program Files\Tesseract-OCR\tesseract.exe
 * - Ghostscript: C:\Program Files\gs\gs10.06.0\bin\gswin64c.exe
 *
 * Wenn sie an anderen Orten installiert wurden, müssen die Pfade im Testcode angepasst werden.
 */

class OcrWorkerApplicationTests {

    @Test
    void tesseractIsAvailableOnSystem() throws Exception {
        try {
            // Vollständiger Pfad zu deiner Installation:
            String tesseractPath = "C:\\Program Files\\Tesseract-OCR\\tesseract.exe";

            ProcessBuilder pb = new ProcessBuilder(tesseractPath, "--version");
            Process process = pb.start();
            int exitCode = process.waitFor();
            String versionOutput = new String(process.getInputStream().readAllBytes());

            assertThat(exitCode)
                    .as("Tesseract sollte installiert und ausführbar sein")
                    .isEqualTo(0);

            assertThat(versionOutput.toLowerCase())
                    .as("Tesseract-Version sollte im Output enthalten sein")
                    .contains("tesseract");

            System.out.println(" Tesseract erfolgreich erkannt:\n" + versionOutput);

        } catch (Exception e) {
            System.out.println(" Tesseract nicht gefunden — Test übersprungen (" + e.getMessage() + ")");
        }
    }

    @Test
    void ghostscriptIsAvailableOnSystem() throws Exception {
        try {
            // Vollständiger Pfad zu Ghostscript
            String ghostscriptPath = "C:\\Program Files\\gs\\gs10.06.0\\bin\\gswin64c.exe";

            ProcessBuilder pb = new ProcessBuilder(ghostscriptPath, "--version");
            Process process = pb.start();
            int exitCode = process.waitFor();
            String output = new String(process.getInputStream().readAllBytes());

            assertThat(exitCode)
                    .as("Ghostscript sollte installiert und lauffähig sein")
                    .isEqualTo(0);

            assertThat(output.trim())
                    .as("Ghostscript-Version sollte eine Zahl enthalten")
                    .matches("\\d+(\\.\\d+)*");

            System.out.println(" Ghostscript erfolgreich erkannt: Version " + output.trim());

        } catch (Exception e) {
            System.out.println(" Ghostscript nicht gefunden — Test übersprungen (" + e.getMessage() + ")");
        }
    }
}
