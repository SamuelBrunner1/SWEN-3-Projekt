package at.technikum.ocrworker;

import at.technikum.ocrworker.messaging.DocumentUploadedMessage;
import at.technikum.ocrworker.service.OcrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqListener {

    private static final Logger log = LoggerFactory.getLogger(RabbitMqListener.class);
    private final OcrService ocrService;

    public RabbitMqListener(OcrService ocrService) {
        this.ocrService = ocrService;
    }

    @RabbitListener(queues = "ocr.incoming")
    public void handle(DocumentUploadedMessage msg) {
        try {
            String text = ocrService.ocrPdf(msg.objectKey());
            String snippet = text.replaceAll("\\s+", " ").trim();
            if (snippet.length() > 160) snippet = snippet.substring(0, 160) + "…";
            log.info("OCR OK docId={} key={} text='{}'", msg.documentId(), msg.objectKey(), snippet);
        } catch (Exception e) {
            log.error("OCR FAILED docId={} key={} err={}", msg.documentId(), msg.objectKey(), e.toString(), e);
            throw new RuntimeException(e);
        }
    }
}
