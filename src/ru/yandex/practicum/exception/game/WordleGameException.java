package ru.yandex.practicum.exception.game;

public class WordleGameException extends Exception {

    public WordleGameException(String message) {
        super(message);
    }

    public WordleGameException(String message, Throwable cause) {
        super(message, cause);
    }
}