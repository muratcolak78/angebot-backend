// src/main/java/com/angebot/backend/exception/NotFoundException.java
package com.angebot.backend.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
