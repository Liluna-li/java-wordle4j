package ru.yandex.practicum.exception.game;

public class WrongWordLengthException extends WordleGameException {

    public WrongWordLengthException(String message) {
        super(message);
    }
}