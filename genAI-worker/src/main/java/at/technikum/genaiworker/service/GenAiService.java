package at.technikum.genaiworker.service;

import at.technikum.genaiworker.messaging.GenAiMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

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

    private final RestTemplate restTemplate = new RestTemplate();

    public void processGenAi(GenAiMessage message) {
        try {
            log.info("Processing GenAI for document {} with model {}.", message.getDocumentId(), model);

            // 1) Prompt bauen
            String prompt = """
                    Du erhältst OCR-Text eines Dokuments.
                    Erstelle eine kurze, prägnante Zusammenfassung auf Deutsch.
                    
                    OCR-Text:
                    %s
                    """.formatted(message.getOcrText());

            // 2) Request für OpenAI bauen
            OpenAiChatRequest requestBody = new OpenAiChatRequest(
                    model,
                    List.of(
                            new OpenAiMessage("system", "Du bist ein hilfreicher Assistent, der Texte knapp auf Deutsch zusammenfasst."),
                            new OpenAiMessage("user", prompt)
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<OpenAiChatRequest> httpEntity = new HttpEntity<>(requestBody, headers);

            // 3) Anfrage an OpenAI schicken
            ResponseEntity<OpenAiChatResponse> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    httpEntity,
                    OpenAiChatResponse.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("OpenAI-Request fehlgeschlagen: Status={}, Body={}",
                        response.getStatusCode(), response.getBody());
                return;
            }

            String summary = extractSummary(response.getBody());
            log.info("Received summary for document {}: {}", message.getDocumentId(), summary);

            // 4) Summary im REST-Service speichern
            sendSummaryToRest(message.getDocumentId(), summary);

        } catch (Exception e) {
            log.error("Fehler bei GenAI-Verarbeitung für Dokument {}: {}",
                    message.getDocumentId(), e.getMessage(), e);
        }
    }

    private String extractSummary(OpenAiChatResponse body) {
        if (body == null || body.choices == null || body.choices.isEmpty()) {
            return "";
        }
        OpenAiChoice first = body.choices.get(0);
        if (first.message == null || first.message.content == null) {
            return "";
        }
        return first.message.content.trim();
    }

    private void sendSummaryToRest(Long documentId, String summary) {
        String url = restBaseUrl + "/api/dokumente/" + documentId + "/summary";

        Map<String, String> payload = Map.of("summary", summary);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                entity,
                Void.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Summary für Dokument {} erfolgreich im REST-Service gespeichert.", documentId);
        } else {
            log.error("Konnte Summary für Dokument {} nicht speichern. Status={}",
                    documentId, response.getStatusCode());
        }
    }

    // ====== Hilfs-DTOs für OpenAI ======

    public static class OpenAiChatRequest {
        public String model;
        public List<OpenAiMessage> messages;

        public OpenAiChatRequest() {
        }

        public OpenAiChatRequest(String model, List<OpenAiMessage> messages) {
            this.model = model;
            this.messages = messages;
        }
    }

    public static class OpenAiMessage {
        public String role;
        public String content;

        public OpenAiMessage() {
        }

        public OpenAiMessage(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }

    public static class OpenAiChatResponse {
        public List<OpenAiChoice> choices;
    }

    public static class OpenAiChoice {
        public OpenAiMessage message;
    }
}

