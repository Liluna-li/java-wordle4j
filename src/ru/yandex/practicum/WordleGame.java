package ru.yandex.practicum;

import ru.yandex.practicum.exception.game.InvalidWordException;
import ru.yandex.practicum.exception.game.WordNotFoundInDictionaryException;
import ru.yandex.practicum.exception.game.WordleGameException;
import ru.yandex.practicum.exception.game.WrongWordLengthException;

import java.io.PrintWriter;
import java.util.*;

public class WordleGame {

    public static final int MAX_STEPS = 6;

    private final WordleDictionary dictionary;
    private final PrintWriter log;
    private final String answer;
    private final Map<String, String> results = new LinkedHashMap<>();
    private final Set<String> hintHistory = new LinkedHashSet<>();

    private int steps = MAX_STEPS;
    private boolean finished;
    private boolean win;

    public WordleGame(WordleDictionary dictionary, PrintWriter log, Random random) {
        this.dictionary = dictionary;
        this.log = log;
        this.answer = dictionary.getRandomWord(random);
    }

    public WordleGame(WordleDictionary dictionary, PrintWriter log, String answer) {
        this.dictionary = dictionary;
        this.log = log;
        this.answer = WordleDictionary.normalize(answer);
        if (this.answer.length() != WordleDictionary.WORD_LENGTH || !WordleDictionary.isCorrectLetters(this.answer)) {
            throw new IllegalArgumentException("Загаданное слово некорректно: " + answer);
        }
    }

    public String makeMove(String input) throws WordleGameException {
        if (finished) {
            throw new WordleGameException("Игра уже завершена");
        }
        if (steps <= 0) {
            throw new WordleGameException("Попытки закончились");
        }

        String word = WordleDictionary.normalize(input);
        if (word.length() != WordleDictionary.WORD_LENGTH) {
            throw new WrongWordLengthException("Слово должно состоять из " + WordleDictionary.WORD_LENGTH
                    + " букв, введено " + word.length());
        }
        if (!WordleDictionary.isCorrectLetters(word)) {
            throw new InvalidWordException("В слове должны быть только русские буквы: " + word);
        }

        boolean isAnswer = word.equals(answer);
        if (!isAnswer && !dictionary.contains(word)) {
            throw new WordNotFoundInDictionaryException("Слово отсутствует в словаре: " + word);
        }

        steps--;
        if (steps < 0) {
            throw new RuntimeException("Некорректное количество оставшихся попыток: " + steps);
        }

        String result = WordleDictionary.compareWords(word, answer);
        results.put(word, result);

        if (isAnswer) {
            win = true;
            finished = true;
        } else if (steps == 0) {
            finished = true;
        }

        if (log != null) {
            log.println("Ход: " + word + " -> " + result + ", осталось попыток: " + steps);
        }
        return result;
    }

    public String requestHint() throws WordleGameException {
        if (finished) {
            throw new WordleGameException("Игра уже завершена");
        }

        String candidate = findCandidate(false);
        if (candidate == null) {
            candidate = findCandidate(true);
        }
        if (candidate == null) {
            throw new RuntimeException("Не удалось подобрать подходящее слово-подсказку");
        }

        hintHistory.add(candidate);
        if (log != null) {
            log.println("Подсказка: " + candidate);
        }
        return candidate;
    }

    private String findCandidate(boolean ignoreHintHistory) {
        for (String word : dictionary.getWords()) {
            if (results.containsKey(word)) {
                continue;
            }
            if (!ignoreHintHistory && hintHistory.contains(word)) {
                continue;
            }
            if (isConsistent(word)) {
                return word;
            }
        }
        return null;
    }

    private boolean isConsistent(String candidate) {
        for (Map.Entry<String, String> entry : results.entrySet()) {
            if (!WordleDictionary.compareWords(entry.getKey(), candidate).equals(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isWin() {
        return win;
    }

    public int getSteps() {
        return steps;
    }

    public String getAnswer() {
        return answer;
    }

    public Map<String, String> getResults() {
        return results;
    }
}