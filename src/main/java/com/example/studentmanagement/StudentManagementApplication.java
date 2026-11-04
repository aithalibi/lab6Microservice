package com.example.studentmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entree de l'application Spring Boot (Etape 6 du TP).
 * L'annotation @SpringBootApplication active la configuration automatique,
 * le scan des composants et demarre le serveur Tomcat embarque.
 */
@SpringBootApplication
public class StudentManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentManagementApplication.class, args);
    }
}
