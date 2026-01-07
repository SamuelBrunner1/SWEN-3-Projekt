/*package at.technikum.ocrworker;

import at.technikum.ocrworker.messaging.GenAiMessage;
import at.technikum.ocrworker.messaging.OcrRequestMessage;
import at.technikum.ocrworker.service.OcrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqListener {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqListener.class);

    public static final String QUEUE_NAME = "ocr.incoming";

    // GenAI-Queue – kannst du so lassen, wenn du sie schon so verwendest
    public static final String GENAI_QUEUE = "genai-queue";

    private final OcrService ocrService;
    private final RabbitTemplate rabbitTemplate;

    public RabbitMqListener(OcrService ocrService, RabbitTemplate rabbitTemplate) {
        this.ocrService = ocrService;
        this.rabbitTemplate = rabbitTemplate;
    }

    // das ist deine bestehende OCR-Queue
    @RabbitListener(queues = QUEUE_NAME)
    public void handleOcrRequest(OcrRequestMessage msg) throws Exception {
        Long documentId = msg.getDocumentId();
        String objectKey = msg.getObjectKey();

        String text = ocrService.ocrPdf(objectKey);
        log.info("OCR OK docId={} key={} text='{}'", documentId, objectKey, text);

        // ➜ NEU: Ergebnis an GenAI-Worker weitergeben
        GenAiMessage genAiMessage = new GenAiMessage(documentId, text);
        rabbitTemplate.convertAndSend(GENAI_QUEUE, genAiMessage);

        log.info("Sent GenAI message for document {} to queue {}", documentId, GENAI_QUEUE);
    }
}*/