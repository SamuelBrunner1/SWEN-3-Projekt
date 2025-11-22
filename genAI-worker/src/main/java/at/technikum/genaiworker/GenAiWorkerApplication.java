package at.technikum.genaiworker;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class GenAiWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GenAiWorkerApplication.class, args);
    }
}
