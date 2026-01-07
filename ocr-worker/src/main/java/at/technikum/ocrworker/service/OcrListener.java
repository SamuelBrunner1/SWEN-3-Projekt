package at.technikum.ocrworker.service;

import at.technikum.ocrworker.messaging.GenAiMessage;
import at.technikum.ocrworker.messaging.OcrRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OcrListener {

    private static final Logger log = LoggerFactory.getLogger(OcrListener.class);

    public static final String OCR_INCOMING_QUEUE = "ocr.incoming";
    public static final String GENAI_QUEUE = "genai-queue";

    private final OcrService ocrService;
    private final RabbitTemplate rabbitTemplate;

    public OcrListener(OcrService ocrService, RabbitTemplate rabbitTemplate) {
        this.ocrService = ocrService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = OCR_INCOMING_QUEUE)
    public void handleOcrRequest(OcrRequestMessage message) throws Exception {
        Long documentId = message.getDocumentId();
        String objectKey = message.getObjectKey();

        log.info("Received OCR request for document {}, objectKey={}", documentId, objectKey);

        // 1) OCR auf dem PDF ausführen
        String ocrText = ocrService.ocrPdf(objectKey);

        // 2) Ergebnis an GenAI-Queue schicken
        GenAiMessage genAiMessage = new GenAiMessage(documentId, ocrText);
        rabbitTemplate.convertAndSend(
                "paperless.documents",
                "document.ocr.completed",
                genAiMessage
        );
        log.info("Sent GenAI message for document {} to '{}'", documentId, GENAI_QUEUE);
    }
}