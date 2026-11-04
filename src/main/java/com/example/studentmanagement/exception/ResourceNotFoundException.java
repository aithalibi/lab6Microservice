package com.example.studentmanagement.exception;

/**
 * Exception metier levee lorsqu'une ressource demandee n'existe pas.
 * Traduite en HTTP 404 Not Found par le GlobalExceptionHandler.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
