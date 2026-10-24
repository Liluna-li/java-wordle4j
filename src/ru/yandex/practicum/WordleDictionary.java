package ru.yandex.practicum;

import ru.yandex.practicum.exception.system.EmptyDictionaryException;
import ru.yandex.practicum.exception.system.WordleSystemException;

import java.util.*;

public class WordleDictionary {

    public static final int WORD_LENGTH = 5;

    private final List<String> words;
    private final Set<String> wordSet;

    public WordleDictionary(List<String> words) {
        if (words == null) {
            throw new WordleSystemException("Список слов не может быть null");
        }
        List<String> normalized = new ArrayList<>();
        Set<String> unique = new LinkedHashSet<>();
        for (String word : words) {
            if (word == null) {
                continue;
            }
            String value = normalize(word);
            if (unique.add(value)) {
                normalized.add(value);
            }
        }
        this.words = Collections.unmodifiableList(normalized);
        this.wordSet = Collections.unmodifiableSet(unique);
    }

    public boolean contains(String word) {
        if (word == null) {
            return false;
        }
        return wordSet.contains(normalize(word));
    }

    public int size() {
        return words.size();
    }

    public List<String> getWords() {
        return words;
    }

    public String getRandomWord(Random random) {
        if (words.isEmpty()) {
            throw new EmptyDictionaryException("Словарь пуст, выбрать загаданное слово невозможно");
        }
        return words.get(random.nextInt(words.size()));
    }

    public static String normalize(String word) {
        if (word == null) {
            return "";
        }
        return word.trim().toLowerCase().replace('ё', 'е');
    }

    public static boolean isCorrectLetters(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (!(c >= 'а' && c <= 'я')) {
                return false;
            }
        }
        return true;
    }

    public static String compareWords(String guess, String answer) {
        if (guess == null || answer == null || guess.length() != WORD_LENGTH || answer.length() != WORD_LENGTH) {
            throw new IllegalArgumentException("Сравниваемые слова должны состоять ровно из " + WORD_LENGTH + " букв");
        }

        StringBuilder result = new StringBuilder(WORD_LENGTH);
        boolean[] matched = new boolean[WORD_LENGTH];

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (guess.charAt(i) == answer.charAt(i)) {
                result.append('+');
                matched[i] = true;
            } else {
                result.append(' ');
            }
        }

        for (int i = 0; i < WORD_LENGTH; i++) {
            if (result.charAt(i) == '+') {
                continue;
            }
            char letter = guess.charAt(i);
            int position = findUnmatchedOccurrence(answer, letter, matched);
            if (position >= 0) {
                matched[position] = true;
                result.setCharAt(i, '^');
            } else {
                result.setCharAt(i, '-');
            }
        }

        return result.toString();
    }

    private static int findUnmatchedOccurrence(String answer, char letter, boolean[] matched) {
        for (int i = 0; i < answer.length(); i++) {
            if (!matched[i] && answer.charAt(i) == letter) {
                return i;
            }
        }
        return -1;
    }
}