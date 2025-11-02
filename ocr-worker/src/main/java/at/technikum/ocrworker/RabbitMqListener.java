package at.technikum.ocrworker;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqListener {

    @RabbitListener(queues = "ocr.incoming") //alt -- dokument-queue
    public void receiveMessage(String message) {
        System.out.println("Jawoi, Received message in OCR Worker: " + message);
    }
}
