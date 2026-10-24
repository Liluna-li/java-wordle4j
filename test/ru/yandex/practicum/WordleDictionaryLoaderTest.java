package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.yandex.practicum.exception.system.DictionaryFileNotFoundException;
import ru.yandex.practicum.exception.system.EmptyDictionaryException;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryLoaderTest {

    @TempDir
    Path tempDir;

    private static PrintWriter log;

    @BeforeAll
    static void setUp() {
        log = new PrintWriter(System.out, true);
    }

    @Test
    void loadFiltersAndNormalizesWords() throws IOException {
        Path file = tempDir.resolve("words.txt");
        Files.writeString(file,
                "Абак\nгонец\nгерой\nЁжик\nАРБУЗ\nhello\nдень\nёжики\n",
                StandardCharsets.UTF_8);

        WordleDictionary dictionary = new WordleDictionaryLoader(log).load(file.toString());

        assertEquals(4, dictionary.size());
        assertTrue(dictionary.contains("гонец"));
        assertTrue(dictionary.contains("герой"));
        assertTrue(dictionary.contains("арбуз"));
        assertTrue(dictionary.contains("ежики"));
        assertFalse(dictionary.contains("абак"));
        assertFalse(dictionary.contains("hello"));
    }

    @Test
    void loadMissingFileThrows() {
        assertThrows(DictionaryFileNotFoundException.class,
                () -> new WordleDictionaryLoader(log).load(tempDir.resolve("missing.txt").toString()));
    }

    @Test
    void loadEmptyDictionaryThrows() throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.writeString(file, "мир\nдень\nа\n", StandardCharsets.UTF_8);

        assertThrows(EmptyDictionaryException.class,
                () -> new WordleDictionaryLoader(log).load(file.toString()));
    }

    @Test
    void loadRealDictionaryHasFiveLetterWords() {
        WordleDictionary dictionary = new WordleDictionaryLoader(log).load("words_ru.txt");

        assertTrue(dictionary.size() > 0);
        assertTrue(dictionary.contains("герой"));
        for (String word : dictionary.getWords()) {
            assertEquals(WordleDictionary.WORD_LENGTH, word.length());
        }
    }
}