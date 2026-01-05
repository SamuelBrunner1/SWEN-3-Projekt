package at.technikum.ocrworker;

import at.technikum.ocrworker.messaging.GenAiMessage;
import at.technikum.ocrworker.messaging.OcrRequestMessage;
import at.technikum.ocrworker.service.OcrService;
import at.technikum.ocrworker.service.ElasticSearchIndexService; // ➜ NEU
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqListener {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqListener.class);

    public static final String QUEUE_NAME = "ocr.incoming";
    public static final String GENAI_QUEUE = "genai-queue";

    private final OcrService ocrService;
    private final RabbitTemplate rabbitTemplate;
    private final ElasticSearchIndexService elasticSearchIndexService; // ➜ NEU

    //Konstruktor ERWEITERT, nichts entfernt
    public RabbitMqListener(
            OcrService ocrService,
            RabbitTemplate rabbitTemplate,
            ElasticSearchIndexService elasticSearchIndexService // ➜ NEU
    ) {
        this.ocrService = ocrService;
        this.rabbitTemplate = rabbitTemplate;
        this.elasticSearchIndexService = elasticSearchIndexService; // ➜ NEU
    }

    @RabbitListener(queues = QUEUE_NAME)
    public void handleOcrRequest(OcrRequestMessage msg) throws Exception {
        log.info(">>> RabbitMqListener TRIGGERED for document {}", msg.getDocumentId());

        Long documentId = msg.getDocumentId();
        String objectKey = msg.getObjectKey();

        String text = ocrService.ocrPdf(objectKey);
        log.info("OCR OK docId={} key={}", documentId, objectKey);

        //OCR-Text in ElasticSearch indexieren
        elasticSearchIndexService.indexDocument(
                documentId,
                objectKey,
                text
        );

        //Ergebnis an GenAI-Worker weitergeben
        GenAiMessage genAiMessage = new GenAiMessage(documentId, text);
        rabbitTemplate.convertAndSend(GENAI_QUEUE, genAiMessage);

        log.info("Sent GenAI message for document {} to queue {}", documentId, GENAI_QUEUE);
    }
}
