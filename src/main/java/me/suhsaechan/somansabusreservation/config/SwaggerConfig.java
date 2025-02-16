package me.suhsaechan.somansabusreservation.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(
        title = "📚SOMANSA-BUS-AUTO-RESERVATION 📚",
        description = """
            ### 🌐 SOMANSA-BUS-AUTO-RESERVATION 🌐 : http:suh-project.synology.me:8091
            
            ## 소만사 버스 자동 예약 시스템
            """,
        version = "1.0v"
    ),
    servers = {
//        @Server(url = "https://api.plane-accident-finder.world", description = "메인 서버 (HTTPS)"),
//        @Server(url = "https://api.test.plane-accident-finder.world", description = "테스트 서버 (HTTPS)"),
        @Server(url = "http:suh-project.synology.me:8091", description = "메인 서버 (HTTP) "),
        @Server(url = "http:suh-project.synology.me:8092", description = "테스트 서버 (HTTP) "),
        @Server(url = "http://localhost:8080", description = "로컬 서버")
    }
)
@Configuration
public class SwaggerConfig {

  @Bean
  public OpenAPI openAPI() {
    // JWT Bearer 인증 스키마 선언
    SecurityScheme jwtAuthScheme = new SecurityScheme()
        .type(SecurityScheme.Type.HTTP)
        .scheme("bearer")
        .bearerFormat("JWT");

    // Swagger UI 에서 기본적으로 적용할 보안 요구 사항 지정
    SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

    return new OpenAPI()
        .components(new Components()
            .addSecuritySchemes("bearerAuth", jwtAuthScheme)
        )
        .addSecurityItem(securityRequirement)
        .servers(List.of(
//            new io.swagger.v3.oas.models.servers.Server()
//                .url("https://api.plane-accident-finder.world")
//                .description("메인 서버 (HTTPS)"),
//            new io.swagger.v3.oas.models.servers.Server()
//                .url("https://api.test.plane-accident-finder.world")
//                .description("테스트 서버 (HTTPS)"),
            new io.swagger.v3.oas.models.servers.Server()
                .url("https://suh-project.synology.me:8091")
                .description("메인 서버 (HTTP)"),
            new io.swagger.v3.oas.models.servers.Server()
                .url("https://suh-project.synology.me:8092")
                .description("테스트 서버 (HTTP)"),
            new io.swagger.v3.oas.models.servers.Server()
                .url("http://localhost:8080")
                .description("로컬 서버")
        ));
  }
}
