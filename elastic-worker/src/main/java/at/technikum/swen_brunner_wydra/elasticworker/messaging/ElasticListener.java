package at.technikum.swen_brunner_wydra.elasticworker.messaging;

import at.technikum.swen_brunner_wydra.elasticworker.config.RabbitMqConfig;
import at.technikum.swen_brunner_wydra.elasticworker.service.ElasticIndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ElasticListener {

    private static final Logger log = LoggerFactory.getLogger(ElasticListener.class);

    private final ElasticIndexService indexService;

    public ElasticListener(ElasticIndexService indexService) {
        this.indexService = indexService;
    }

    @RabbitListener(queues = RabbitMqConfig.ELASTIC_QUEUE)
    public void handleOcrResult(GenAiMessage message) {
        log.info("Received OCR text for indexing: docId={}, length={}",
                message.getDocumentId(),
                message.getOcrText() != null ? message.getOcrText().length() : 0
        );

        indexService.indexOcrText(message.getDocumentId(), message.getOcrText());
    }
}
