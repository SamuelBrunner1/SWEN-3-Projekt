package at.technikum.swen_brunner_wydra.controller;

import at.technikum.swen_brunner_wydra.config.RabbitConfig;
import at.technikum.swen_brunner_wydra.entity.Dokument;
import at.technikum.swen_brunner_wydra.messaging.DocumentUploadedMessage;
import at.technikum.swen_brunner_wydra.repository.DokumentRepository;
import at.technikum.swen_brunner_wydra.service.StorageService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final StorageService storage;
    private final DokumentRepository repo;
    private final RabbitTemplate rabbit;

    public UploadController(StorageService storage, DokumentRepository repo, RabbitTemplate rabbit) {
        this.storage = storage;
        this.repo = repo;
        this.rabbit = rabbit;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long upload(@RequestPart("file") MultipartFile file) throws Exception {
        if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
            throw new IllegalArgumentException("Nur PDFs werden akzeptiert.");
        }

        String key = storage.putPdf(file.getOriginalFilename(), file.getInputStream(), file.getSize());

        // minimale DB-Persistenz (Passe Felder an dein Entity an)
        Dokument d = new Dokument();
        d.setTitel(file.getOriginalFilename());
        d.setDateiname(key);
        Dokument saved = repo.save(d);

        var msg = new DocumentUploadedMessage(
                saved.getId(),
                storage.bucket(),
                key,
                file.getOriginalFilename(),
                "application/pdf",
                Instant.now().toString()
        );

        rabbit.convertAndSend(RabbitConfig.EXCHANGE_NAME, RabbitConfig.ROUTING_KEY, msg);
        return saved.getId();
    }
}
