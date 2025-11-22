package at.technikum.genaiworker.service;

import at.technikum.genaiworker.messaging.GenAiMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GenAiService {

    private static final Logger log = LoggerFactory.getLogger(GenAiService.class);

    @Value("${genai.api-key}")
    private String apiKey;

    @Value("${genai.model}")
    private String model;

    @Value("${genai.url}")
    private String apiUrl;

    @Value("${rest.base-url}")
    private String restBaseUrl;

    public void processGenAi(GenAiMessage message) {
        log.info("Processing GenAI for document {} with model {}.", message.getDocumentId(), model);

        // TODO:
        // 1) HTTP-Request an OpenAI schicken
        // 2) Summary aus der Response holen
        // 3) Summary per REST im app-Service speichern
    }
}
