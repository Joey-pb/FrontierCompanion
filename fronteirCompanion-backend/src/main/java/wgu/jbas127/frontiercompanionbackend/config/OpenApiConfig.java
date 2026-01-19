package wgu.jbas127.frontiercompanionbackend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Frontier Culture Museum API")
                        .version("1.0")
                        .description("API for museum content, search, and analytics"))
                .components(new Components()
                        .addSecuritySchemes("api-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .name("X-API-Key"))
                        .addSecuritySchemes("basic-auth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic")));
    }
}
