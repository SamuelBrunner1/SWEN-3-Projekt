package at.technikum.genaiworker.messaging;

import at.technikum.genaiworker.config.RabbitMqConfig;
import at.technikum.genaiworker.service.GenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class GenAiListener {

    private static final Logger log = LoggerFactory.getLogger(GenAiListener.class);

    private final GenAiService genAiService;

    public GenAiListener(GenAiService genAiService) {
        this.genAiService = genAiService;
    }

    @RabbitListener(queues = RabbitMqConfig.GENAI_QUEUE)
    public void handle(GenAiMessage message) {
        log.info("Received GenAI message for document {}.", message.getDocumentId());
        genAiService.processGenAi(message);
    }
}
