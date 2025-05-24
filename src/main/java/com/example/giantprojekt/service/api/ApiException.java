package com.example.giantprojekt.service.api;

/** Обёртка для всех ошибок вызова Pterodactyl-API. */
public class ApiException extends Exception {

    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
