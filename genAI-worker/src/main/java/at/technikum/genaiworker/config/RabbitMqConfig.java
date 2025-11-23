package at.technikum.genaiworker.config;

import at.technikum.genaiworker.messaging.GenAiMessage;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMqConfig {

    public static final String GENAI_QUEUE = "genai-queue";

    @Bean
    public Queue genAiQueue() {
        return new Queue(GENAI_QUEUE, true);
    }

    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter() {
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();

        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();

        // TypeId vom OCR-Worker → lokale DTO-Klasse im GenAI-Worker
        idClassMapping.put(
                "at.technikum.ocrworker.messaging.GenAiMessage",
                GenAiMessage.class
        );

        classMapper.setIdClassMapping(idClassMapping);
        converter.setClassMapper(classMapper);

        return converter;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            JacksonJsonMessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory =
                new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }
}
