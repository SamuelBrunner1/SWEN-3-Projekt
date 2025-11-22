package at.technikum.ocrworker.service;

import at.technikum.ocrworker.messaging.GenAiMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OcrListener {

    private static final Logger log = LoggerFactory.getLogger(OcrListener.class);
    public static final String GENAI_QUEUE = "genai-queue";

    private final OcrService ocrService;
    private final RabbitTemplate rabbitTemplate;

    public OcrListener(OcrService ocrService, RabbitTemplate rabbitTemplate) {
        this.ocrService = ocrService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "swen_queue") // deinen Queue-Namen aus RabbitConfig nehmen
    public void handleOcrRequest(Long documentId) throws Exception {
        log.info("Received OCR request for document {}", documentId);

        // TODO: hier den richtigen MinIO-Object-Key ermitteln
        String objectKey = /* wie bisher */ null;

        String ocrText = ocrService.ocrPdf(objectKey);

        GenAiMessage msg = new GenAiMessage(documentId, ocrText);
        rabbitTemplate.convertAndSend(GENAI_QUEUE, msg);

        log.info("Sent GenAI message for document {}", documentId);
    }
}
