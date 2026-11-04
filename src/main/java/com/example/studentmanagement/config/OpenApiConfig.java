package com.example.studentmanagement.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration Swagger / OpenAPI 3 (Etape 9 du TP).
 * springdoc-openapi detecte automatiquement les @RestController ; ce bean
 * enrichit seulement les metadonnees affichees dans Swagger UI.
 *
 * Interface : http://localhost:8080/swagger-ui.html
 * Contrat   : http://localhost:8080/v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String port;

    @Bean
    public OpenAPI studentManagementOpenAPI() {
        Contact contact = new Contact()
                .name("Yasmine Ait Halibi")
                .email("aithalibi@example.com");

        License license = new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0.html");

        Info info = new Info()
                .title("Student Management API")
                .version("1.0.0")
                .description("API REST de gestion des etudiants documentee avec springdoc-openapi. "
                        + "TP 6 - Spring Boot et Swagger (Architecture Microservices).")
                .contact(contact)
                .license(license);

        Server localServer = new Server()
                .url("http://localhost:" + port)
                .description("Serveur de developpement local");

        ExternalDocumentation doc = new ExternalDocumentation()
                .description("Documentation springdoc-openapi")
                .url("https://springdoc.org/");

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer))
                .externalDocs(doc);
    }
}
