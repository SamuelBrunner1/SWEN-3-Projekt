package at.technikum.brunnerwydraswenprojekt.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info()
                .title("Brunner-Wydra SWEN Projekt API")
                .version("1.0.0")
                .description("Dokumenten-API (Demo)"));
    }
}
