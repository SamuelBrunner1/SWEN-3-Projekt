package at.technikum.ocrworker;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public Queue dokumentQueue() {
        // durable = true, Queue bleibt auch nach Neustart erhalten
        return new Queue("dokument-queue", true);
    }
}
