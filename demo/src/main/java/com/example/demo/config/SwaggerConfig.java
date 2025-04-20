package com.example.demo.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        return new OpenAPI()
                .info(new Info().title("🏋️ 健身紀錄系統 API 文件").version("1.0")
                    .description("這裡列出所有可用的 API 端點，包含輸入格式與回傳格式，可即時測試。"))
                    .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                    .components(new Components()
                        .addSecuritySchemes(securitySchemeName, 
                            new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                        )
                    );
    }
} 