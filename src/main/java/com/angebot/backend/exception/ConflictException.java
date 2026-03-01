// src/main/java/com/angebot/backend/exception/ConflictException.java
package com.angebot.backend.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
