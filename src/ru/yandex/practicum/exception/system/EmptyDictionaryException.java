package ru.yandex.practicum.exception.system;

public class EmptyDictionaryException extends WordleSystemException {

    public EmptyDictionaryException(String message) {
        super(message);
    }
}