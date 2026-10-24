package ru.yandex.practicum;

import ru.yandex.practicum.exception.game.WordleGameException;
import ru.yandex.practicum.exception.system.LogFileCreateException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Scanner;

public class Wordle {

    private static final String DICTIONARY_FILE = "words_ru.txt";
    private static final String LOG_FILE = "wordle.log";

    public static void main(String[] args) {
        try {
            run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void run() {
        try (PrintWriter log = createLog()) {
            try {
                startGame(log);
            } catch (Exception e) {
                e.printStackTrace(log);
            }
        }
    }

    private static PrintWriter createLog() {
        try {
            FileOutputStream fos = new FileOutputStream(LOG_FILE);
            OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            return new PrintWriter(osw);
        } catch (IOException e) {
            throw new LogFileCreateException("Не удалось создать лог-файл " + LOG_FILE, e);
        }
    }

    private static void startGame(PrintWriter log) {
        WordleDictionaryLoader loader = new WordleDictionaryLoader(log);
        WordleDictionary dictionary = loader.load(DICTIONARY_FILE);
        WordleGame game = new WordleGame(dictionary, log, new Random());
        play(game);
    }

    private static void play(WordleGame game) {
        System.out.println("Добро пожаловать в Wordle!");
        System.out.println("Загадано слово из " + WordleDictionary.WORD_LENGTH
                + " букв. У вас " + WordleGame.MAX_STEPS + " попыток.");
        System.out.println("Введите слово или нажмите Enter для подсказки.");

        try (Scanner scanner = new Scanner(System.in)) {
            while (!game.isFinished()) {
                System.out.print("> ");
                String input = scanner.nextLine();

                if (input.trim().isEmpty()) {
                    try {
                        System.out.println(game.requestHint());
                    } catch (WordleGameException e) {
                        System.out.println(e.getMessage());
                    }
                    continue;
                }

                try {
                    String result = game.makeMove(input);
                    System.out.println(result);
                    if (game.isFinished() && game.isWin()) {
                        System.out.println("Победа! Загаданное слово " + game.getAnswer() + " отгадано.");
                    }
                } catch (WordleGameException e) {
                    System.out.println(e.getMessage());
                }
            }

            if (!game.isWin()) {
                System.out.println("Попытки закончились. Загаданное слово: " + game.getAnswer());
            }
        }
    }
}