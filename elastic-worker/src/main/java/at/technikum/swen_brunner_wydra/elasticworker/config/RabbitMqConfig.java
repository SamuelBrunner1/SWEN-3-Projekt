package at.technikum.swen_brunner_wydra.elasticworker.config;

import at.technikum.swen_brunner_wydra.elasticworker.messaging.GenAiMessage;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMqConfig {

    public static final String EXCHANGE = "paperless.documents";
    public static final String OCR_COMPLETED_KEY = "document.ocr.completed";
    public static final String ELASTIC_QUEUE = "elastic-queue";

    @Bean
    public Queue elasticQueue() {
        return new Queue(ELASTIC_QUEUE, true);
    }

    @Bean
    public TopicExchange documentExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Binding elasticBinding(Queue elasticQueue, TopicExchange documentExchange) {
        return BindingBuilder.bind(elasticQueue).to(documentExchange).with(OCR_COMPLETED_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();

        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();

        // TypeId vom OCR-Worker → lokale DTO-Klasse im elastic-worker
        idClassMapping.put("at.technikum.ocrworker.messaging.GenAiMessage", GenAiMessage.class);

        classMapper.setIdClassMapping(idClassMapping);
        converter.setClassMapper(classMapper);

        return converter;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter
    ) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }
}
