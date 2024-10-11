package net.allbareum.allbareumbackend.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "올발음 API Swagger",
                description = "API 명세서",
                version = "v1",
                contact = @Contact(
                        name = "오기택",
                        email = "dhrlxor1512@gmail.com"
                )
        )
)
@Configuration
public class OpenApiConfig {
}