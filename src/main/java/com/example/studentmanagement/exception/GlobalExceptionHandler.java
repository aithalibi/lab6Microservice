package com.example.studentmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Gestion centralisee des erreurs : renvoie un JSON lisible au lieu d'une
 * stack trace pour les 404 et les erreurs de validation (starter validation).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corps(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> corps = corps(HttpStatus.BAD_REQUEST, "Validation echouee");
        Map<String, String> erreurs = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            erreurs.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        corps.put("erreurs", erreurs);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corps);
    }

    private Map<String, Object> corps(HttpStatus status, String message) {
        Map<String, Object> corps = new LinkedHashMap<>();
        corps.put("horodatage", LocalDateTime.now().toString());
        corps.put("statut", status.value());
        corps.put("erreur", status.getReasonPhrase());
        corps.put("message", message);
        return corps;
    }
}
