package ru.yandex.practicum;

import ru.yandex.practicum.exception.system.DictionaryFileNotFoundException;
import ru.yandex.practicum.exception.system.EmptyDictionaryException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class WordleDictionaryLoader {

    private final PrintWriter log;

    public WordleDictionaryLoader(PrintWriter log) {
        this.log = log;
    }

    public WordleDictionary load(String fileName) {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = WordleDictionary.normalize(line);
                if (word.length() != WordleDictionary.WORD_LENGTH || !WordleDictionary.isCorrectLetters(word)) {
                    continue;
                }
                words.add(word);
            }
        } catch (IOException e) {
            throw new DictionaryFileNotFoundException("Не удалось прочитать файл словаря: " + fileName, e);
        }

        if (words.isEmpty()) {
            throw new EmptyDictionaryException("Словарь не содержит ни одного слова из "
                    + WordleDictionary.WORD_LENGTH + " букв");
        }

        if (log != null) {
            log.println("Загружен словарь из файла " + fileName + ", подходящих слов: " + words.size());
        }
        return new WordleDictionary(words);
    }
}