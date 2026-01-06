package at.technikum.swen_brunner_wydra.controller;

import at.technikum.swen_brunner_wydra.config.RabbitConfig;
import at.technikum.swen_brunner_wydra.entity.Dokument;
import at.technikum.swen_brunner_wydra.entity.User;
import at.technikum.swen_brunner_wydra.messaging.DocumentUploadedMessage;
import at.technikum.swen_brunner_wydra.repository.DokumentRepository;
import at.technikum.swen_brunner_wydra.repository.UserRepository;
import at.technikum.swen_brunner_wydra.service.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final StorageService storage;
    private final DokumentRepository repo;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbit;

    public UploadController(StorageService storage,
                            DokumentRepository repo,
                            UserRepository userRepository,
                            RabbitTemplate rabbit) {
        this.storage = storage;
        this.repo = repo;
        this.userRepository = userRepository;
        this.rabbit = rabbit;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Long upload(@RequestPart("file") MultipartFile file,
                       HttpServletRequest request) throws Exception {

        // --- Auth: userId aus JwtFilter-Request-Attribut holen ---
        Object v = request.getAttribute("userId");
        Long userId = null;
        if (v instanceof Long l) userId = l;
        else if (v instanceof Integer i) userId = i.longValue();

        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        // --- Validation ---
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Keine Datei hochgeladen.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nur PDFs werden akzeptiert.");
        }

        // --- Upload to MinIO ---
        String key = storage.putPdf(file.getOriginalFilename(), file.getInputStream(), file.getSize());

        // --- Persist Dokument (WICHTIG: user setzen!) ---
        Dokument d = new Dokument();
        d.setTitel(file.getOriginalFilename());
        d.setDateiname(key);
        d.setUser(user);

        Dokument saved = repo.save(d);

        // --- Notify via RabbitMQ ---
        DocumentUploadedMessage msg = new DocumentUploadedMessage(
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
