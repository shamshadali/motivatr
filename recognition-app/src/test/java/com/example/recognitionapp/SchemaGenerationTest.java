package com.example.recognitionapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test") // Optional: can define a test profile if needed
class SchemaGenerationTest {

    @Test
    void contextLoads() {
        // The act of loading the Spring Boot application context
        // with ddl-auto=create will trigger schema generation.
        System.out.println("ApplicationContext loaded. DDL should be in the logs.");
    }
}
