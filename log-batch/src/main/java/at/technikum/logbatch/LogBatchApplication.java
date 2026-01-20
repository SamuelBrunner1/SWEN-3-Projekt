package at.technikum.logbatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LogBatchApplication {

    public static void main(String[] args) {
        SpringApplication.run(LogBatchApplication.class, args);
    }
}
