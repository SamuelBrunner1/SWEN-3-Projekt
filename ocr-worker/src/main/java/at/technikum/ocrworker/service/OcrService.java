package at.technikum.ocrworker.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class OcrService {

    private static final Logger log = LoggerFactory.getLogger(OcrService.class);

    private final MinioClient minio;
    private final String bucket;
    private final String langs;

    public OcrService(MinioClient minio,
                      @Value("${MINIO_BUCKET:documents}") String bucket,
                      @Value("${TESSERACT_LANGS:eng}") String langs) {
        this.minio = minio;
        this.bucket = bucket;
        this.langs  = langs;
    }

    /**
     * Lädt das PDF (objectKey) aus MinIO, rendert es per Ghostscript zu PNG-Seiten und OCRt diese mit Tesseract.
     * @return gesamter erkannter Text (alle Seiten).
     */


    public String ocrPdf(String objectKey) throws Exception {
        Path tmpDir = Files.createTempDirectory("ocr-");
        Path pdfPath = tmpDir.resolve("input.pdf");
        try {
            // 1) PDF aus MinIO laden
            try (InputStream in = minio.getObject(GetObjectArgs.builder()
                    .bucket(bucket).object(objectKey).build())) {
                Files.copy(in, pdfPath, StandardCopyOption.REPLACE_EXISTING);
            }

            // 2) PDF -> PNG (page-001.png, page-002.png, …) via Ghostscript
            String outPattern = tmpDir.resolve("page-%03d.png").toString();
            ExecResult gsRes = run(new String[]{
                    "gs", "-q", "-dSAFER", "-dBATCH", "-dNOPAUSE",
                    "-sDEVICE=pnggray", "-r300",
                    "-sOutputFile=" + outPattern,
                    pdfPath.toString()
            });
            if (gsRes.exitCode != 0) {
                throw new IllegalStateException("Ghostscript failed (" + gsRes.exitCode + "):\n" + gsRes.all());
            }

            // 3) Seiten einsammeln
            List<Path> pages = new ArrayList<>();
            try (Stream<Path> s = Files.list(tmpDir)) {
                s.filter(p -> p.getFileName().toString().toLowerCase().endsWith(".png"))
                        .sorted(Comparator.comparing(p -> p.getFileName().toString()))
                        .forEach(pages::add);
            }
            if (pages.isEmpty()) throw new IllegalStateException("No pages rendered by Ghostscript.");

            // 4) Tesseract auf jede Seite
            StringBuilder full = new StringBuilder();
            for (Path page : pages) {
                ExecResult tr = run(new String[]{
                        "tesseract", page.toString(), "stdout",
                        "-l", langs, "--dpi", "300", "--oem", "1", "--psm", "1"
                });
                if (tr.exitCode != 0) {
                    throw new IllegalStateException("Tesseract failed (" + tr.exitCode + "):\n" + tr.all());
                }
                full.append(tr.stdout);
                if (!tr.stdout.endsWith("\n")) full.append('\n');
            }
            return full.toString();

        } finally {
            // 5) Aufräumen (rekursiv)
            try (Stream<Path> walk = Files.walk(tmpDir)) {
                walk.sorted(Comparator.reverseOrder()).forEach(p -> {
                    try { Files.deleteIfExists(p); } catch (Exception ignored) {}
                });
            } catch (Exception ignored) {}
        }
    }

    // ---------- Helpers ----------

    private ExecResult run(String[] cmd) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(false);
        Process p = pb.start();
        byte[] out = p.getInputStream().readAllBytes();
        byte[] err = p.getErrorStream().readAllBytes();
        int code = p.waitFor();
        String stdout = new String(out, StandardCharsets.UTF_8);
        String stderr = new String(err, StandardCharsets.UTF_8);
        if (code != 0) {
            log.warn("Command failed: {}\nstdout:\n{}\nstderr:\n{}", String.join(" ", cmd), stdout, stderr);
        } else {
            log.debug("Command ok: {}", String.join(" ", cmd));
        }
        return new ExecResult(code, stdout, stderr);
    }

    private static final class ExecResult {
        final int exitCode; final String stdout; final String stderr;
        ExecResult(int code, String out, String err) { this.exitCode = code; this.stdout = out; this.stderr = err; }
        String all() { return "stdout:\n" + stdout + "\n---\nstderr:\n" + stderr; }
    }
}
