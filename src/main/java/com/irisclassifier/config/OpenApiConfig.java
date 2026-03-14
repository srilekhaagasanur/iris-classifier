package com.irisclassifier.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI irisClassifierOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Iris Classifier Microservice API")
                        .description("""
                                A Spring Boot microservice that uses a **K-Nearest Neighbours (KNN)** 
                                classification model trained on the classic **UCI Iris dataset** 
                                to predict the species of an Iris flower from its measurements.
                                
                                ### Species
                                - Iris setosa
                                - Iris versicolor
                                - Iris virginica
                                
                                ### Features
                                - Single and batch classification
                                - Confidence scores and probability distributions
                                - Model metrics endpoint
                                - Input validation
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Iris Classifier")
                                .email("dev@irisclassifier.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development")
                ));
    }
}