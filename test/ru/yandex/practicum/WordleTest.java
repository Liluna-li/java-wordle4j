package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exception.game.InvalidWordException;
import ru.yandex.practicum.exception.game.WordNotFoundInDictionaryException;
import ru.yandex.practicum.exception.game.WordleGameException;
import ru.yandex.practicum.exception.game.WrongWordLengthException;

import java.io.PrintWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {
    private PrintWriter log;

    @BeforeEach
    void setUp() {
        log = new PrintWriter(System.out, true);
    }

    private static WordleDictionary dictionary(List<String> words) {
        return new WordleDictionary(words);
    }

    @Test
    void winningMoveReturnsAllPluses() throws WordleGameException {
        WordleGame game = new WordleGame(dictionary(List.of("герой", "гонец")), log, "герой");

        String result = game.makeMove("герой");

        assertEquals("+++++", result);
        assertTrue(game.isWin());
        assertTrue(game.isFinished());
        assertEquals(WordleGame.MAX_STEPS - 1, game.getSteps());
    }

    @Test
    void wrongWordLengthDoesNotDecrementSteps() {
        WordleGame game = new WordleGame(dictionary(List.of("герой")), log, "герой");

        assertThrows(WrongWordLengthException.class, () -> game.makeMove("мир"));
        assertEquals(WordleGame.MAX_STEPS, game.getSteps());
    }

    @Test
    void nonCyrillicWordThrows() {
        WordleGame game = new WordleGame(dictionary(List.of("герой")), log, "герой");

        assertThrows(InvalidWordException.class, () -> game.makeMove("hello"));
    }

    @Test
    void wordNotInDictionaryThrows() {
        WordleGame game = new WordleGame(dictionary(List.of("герой")), log, "герой");

        assertThrows(WordNotFoundInDictionaryException.class, () -> game.makeMove("слово"));
        assertEquals(WordleGame.MAX_STEPS, game.getSteps());
    }

    @Test
    void losingAfterAllStepsExhausted() throws WordleGameException {
        WordleGame game = new WordleGame(
                dictionary(List.of("герой", "гонец", "книга", "палец", "трава", "ветер", "слово")),
                log, "герой");

        for (String word : List.of("гонец", "книга", "палец", "трава", "ветер", "слово")) {
            game.makeMove(word);
        }

        assertTrue(game.isFinished());
        assertFalse(game.isWin());
        assertEquals(0, game.getSteps());
    }

    @Test
    void moveAfterGameEndsThrows() throws WordleGameException {
        WordleGame game = new WordleGame(dictionary(List.of("герой")), log, "герой");
        game.makeMove("герой");

        assertThrows(WordleGameException.class, () -> game.makeMove("герой"));
    }

    @Test
    void firstHintReturnsFirstDictionaryWord() throws WordleGameException {
        WordleGame game = new WordleGame(
                dictionary(List.of("трава", "герой", "гонец", "книга")), log, "герой");

        assertEquals("трава", game.requestHint());
    }

    @Test
    void hintDoesNotRepeat() throws WordleGameException {
        WordleGame game = new WordleGame(
                dictionary(List.of("трава", "герой", "гонец")), log, "герой");

        String first = game.requestHint();
        String second = game.requestHint();

        assertNotEquals(first, second);
    }

    @Test
    void hintRespectsPreviousMovesAndKeepsAnswer() throws WordleGameException {
        WordleGame game = new WordleGame(
                dictionary(List.of("трава", "герой", "гонец", "книга", "палец")), log, "герой");

        game.makeMove("трава");

        assertEquals("герой", game.requestHint());
    }

    @Test
    void hintIsConsistentWithAllMoves() throws WordleGameException {
        WordleGame game = new WordleGame(
                dictionary(List.of("трава", "герой", "гонец", "книга", "палец", "ветер")), log, "герой");

        game.makeMove("гонец");

        String hint = game.requestHint();

        assertNotNull(hint);
        for (var entry : game.getResults().entrySet()) {
            assertEquals(entry.getValue(), WordleDictionary.compareWords(entry.getKey(), hint));
        }
    }

    @Test
    void computerSolvesGameWithHintsOnly() throws WordleGameException {
        WordleGame game = new WordleGame(
                dictionary(List.of("трава", "герой", "гонец", "книга", "палец", "ветер", "слово")),
                log, "герой");

        int iterations = 0;
        List<String> words = List.of("трава", "герой", "гонец", "книга", "палец", "ветер", "слово");
        while (!game.isFinished()) {
            String hint = game.requestHint();
            assertNotNull(hint);
            game.makeMove(hint);
            iterations++;
            assertTrue(iterations <= WordleGame.MAX_STEPS + words.size(),
                    "Алгоритм не должен уйти в бесконечный цикл");
        }

        assertTrue(game.isWin());
        assertTrue(game.getSteps() >= 0);
    }
}
