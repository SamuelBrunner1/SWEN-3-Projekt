package at.technikum.logbatch.service;

import at.technikum.logbatch.dto.AccessEntryDto;
import at.technikum.logbatch.dto.AccessLogDto;
import at.technikum.logbatch.entity.DocumentAccessStat;
import at.technikum.logbatch.repository.DocumentAccessStatRepository;

import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;


@Service
public class AccessLogBatchService {

    private final DocumentAccessStatRepository repository;
    private Unmarshaller unmarshaller;

    @Value("${batch.input-dir}")
    private String inputDir;

    @Value("${batch.archive-dir}")
    private String archiveDir;

    @Value("${batch.file-pattern}")
    private String filePattern;

    public AccessLogBatchService(DocumentAccessStatRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    void init() throws Exception {
        JAXBContext context = JAXBContext.newInstance(AccessLogDto.class);
        this.unmarshaller = context.createUnmarshaller();
    }

    @Scheduled(cron = "${batch.cron}")
    public void processAccessLogs() {
        try {
            Files.list(Path.of(inputDir))
                    .filter(p -> p.getFileName().toString().matches(filePattern.replace("*", ".*")))
                    .forEach(this::processFile);
        } catch (Exception e) {
            throw new RuntimeException("Error processing access logs", e);
        }
    }

    private void processFile(Path file) {
        try {
            AccessLogDto accessLog =
                    (AccessLogDto) unmarshaller.unmarshal(file.toFile());

            LocalDate accessDate = LocalDate.parse(accessLog.getDate());

            for (AccessEntryDto entry : accessLog.getEntries()) {
                DocumentAccessStat stat =
                        repository.findByDocumentIdAndAccessDate(
                                        entry.getDocumentId(),
                                        accessDate
                                )
                                .orElseGet(() ->
                                        new DocumentAccessStat(
                                                entry.getDocumentId(),
                                                accessDate,
                                                0
                                        )
                                );

                stat.increaseCount(entry.getCount());
                repository.save(stat);
            }

            archiveFile(file);

        } catch (Exception e) {
            throw new RuntimeException("Failed to process file: " + file, e);
        }
    }

    private void archiveFile(Path file) throws Exception {
        Path target = Path.of(archiveDir, file.getFileName().toString());
        Files.createDirectories(target.getParent());
        Files.move(file, target, StandardCopyOption.REPLACE_EXISTING);
    }
}
