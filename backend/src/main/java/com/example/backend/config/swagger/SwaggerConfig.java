package com.example.backend.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {
// http://localhost:7777/swagger-ui/index.html
    @Bean
    public OpenAPI api() {
        return new OpenAPI().info(this.apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Our Own Star")
                .version("1.0")
                .description("우리가 사랑하는 아이돌을 이야기하고 공유하는 특별한 공간")
                ;
    }
}
