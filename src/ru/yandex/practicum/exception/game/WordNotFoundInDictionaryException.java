package ru.yandex.practicum.exception.game;

public class WordNotFoundInDictionaryException extends WordleGameException {

    public WordNotFoundInDictionaryException(String message) {
        super(message);
    }
}