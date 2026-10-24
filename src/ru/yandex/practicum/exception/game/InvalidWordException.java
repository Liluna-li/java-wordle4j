package ru.yandex.practicum.exception.game;

public class InvalidWordException extends WordleGameException {

    public InvalidWordException(String message) {
        super(message);
    }
}