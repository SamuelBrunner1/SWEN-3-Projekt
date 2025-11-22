package at.technikum.genaiworker.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String GENAI_QUEUE = "genai-queue";

    @Bean
    public Queue genAiQueue() {
        return new Queue(GENAI_QUEUE, true);
    }
}
