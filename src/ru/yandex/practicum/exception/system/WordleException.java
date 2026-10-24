package ru.yandex.practicum.exception.system;

public class WordleException extends RuntimeException {

    public WordleException(String message) {
        super(message);
    }

    public WordleException(String message, Throwable cause) {
        super(message, cause);
    }
}