package net.allbareum.allbareumbackend.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
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
        ),
        security = {@SecurityRequirement(name = "bearerAuth")}
        ,
        servers = {
                @Server(url = "https://backend.allbareum.p-e.kr", description = "Production Server")
        }
)

@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@Configuration
public class OpenApiConfig {
}
