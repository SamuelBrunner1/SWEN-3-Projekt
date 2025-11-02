package at.technikum.swen_brunner_wydra.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ-Konfiguration:
 *  - TopicExchange: "paperless.documents"
 *  - Queue: "ocr.incoming"
 *  - Routing-Key: "document.uploaded"
 *  - JSON Message Converter für automatische (De-)Serialisierung
 */
@Configuration
public class RabbitConfig {

    // Konstanten, damit REST- und Worker-Seite dieselben Namen nutzen können
    public static final String EXCHANGE_NAME = "paperless.documents";
    public static final String ROUTING_KEY = "document.uploaded";
    public static final String QUEUE_NAME = "ocr.incoming";

    /** Topic Exchange für alle dokumentbezogenen Nachrichten */
    @Bean
    public TopicExchange documentExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    /** Queue für den OCR-Worker */
    @Bean
    public Queue ocrQueue() {
        // durable = true, damit sie RabbitMQ-Neustarts überlebt
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    /** Binding Queue ↔ Exchange mit Routing-Key */
    @Bean
    public Binding ocrBinding(Queue ocrQueue, TopicExchange documentExchange) {
        return BindingBuilder.bind(ocrQueue)
                .to(documentExchange)
                .with(ROUTING_KEY);
    }

    /** JSON-Konverter, damit RabbitTemplate/Listener automatisch Objekte senden/empfangen können */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
