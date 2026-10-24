package ru.yandex.practicum.exception.system;

public class DictionaryFileNotFoundException extends WordleSystemException {
    public DictionaryFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}