package at.technikum.ocrworker;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    // Gleiche Namen wie in der REST-App, damit alles zusammenspielt
    public static final String EXCHANGE = "paperless.documents";
    public static final String ROUTING_KEY = "document.uploaded";
    public static final String QUEUE = "ocr.incoming";

    /** Optional: Du kannst die folgenden drei Beans entfernen,
     *  wenn du die Deklaration nur in der REST-App machen willst.
     *  Dann MUSS aber der JSON-Converter hier bleiben. */
    @Bean
    public TopicExchange documentExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue ocrQueue() {
        return QueueBuilder.durable(QUEUE).build();
    }

    @Bean
    public Binding ocrBinding(Queue ocrQueue, TopicExchange documentExchange) {
        return BindingBuilder.bind(ocrQueue).to(documentExchange).with(ROUTING_KEY);
    }

    /** Wichtig: JSON (De-)Serialisierung für Nachrichtenobjekte */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
