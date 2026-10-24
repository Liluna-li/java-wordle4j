package ru.yandex.practicum.exception.system;

public class WordleSystemException extends WordleException {

    public WordleSystemException(String message) {
        super(message);
    }

    public WordleSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}