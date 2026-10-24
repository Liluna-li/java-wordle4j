package ru.yandex.practicum;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.exception.system.EmptyDictionaryException;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryTest {

    @Test
    void normalizeConvertsCaseAndYo() {
        assertEquals("елка", WordleDictionary.normalize("Ёлка"));
        assertEquals("арбуз", WordleDictionary.normalize("  АРБУЗ  "));
        assertEquals("ежики", WordleDictionary.normalize("ёжики"));
    }

    @Test
    void normalizeEmptyAndNull() {
        assertEquals("", WordleDictionary.normalize(""));
        assertEquals("", WordleDictionary.normalize(null));
    }

    @Test
    void isCorrectLettersRejectsNonCyrillic() {
        assertTrue(WordleDictionary.isCorrectLetters("слово"));
        assertFalse(WordleDictionary.isCorrectLetters("hello"));
        assertFalse(WordleDictionary.isCorrectLetters(""));
        assertFalse(WordleDictionary.isCorrectLetters(null));
    }

    @Test
    void compareWordsExactMatch() {
        assertEquals("+++++", WordleDictionary.compareWords("герой", "герой"));
    }

    @Test
    void compareWordsExampleFromSpec() {
        assertEquals("+^-^-", WordleDictionary.compareWords("гонец", "герой"));
    }

    @Test
    void compareWordsAllAbsent() {
        assertEquals("-----", WordleDictionary.compareWords("слово", "брать"));
    }

    @Test
    void compareWordsHandlesDuplicateLetters() {
        assertEquals("^-++-", WordleDictionary.compareWords("аббат", "кабан"));
    }

    @Test
    void compareWordsRejectsWrongLength() {
        assertThrows(IllegalArgumentException.class, () -> WordleDictionary.compareWords("мир", "герой"));
    }

    @Test
    void dictionaryDeduplicatesNormalizedWords() {
        WordleDictionary dictionary = new WordleDictionary(List.of("Елка", "ёлка", "елка"));
        assertEquals(1, dictionary.size());
        assertTrue(dictionary.contains("ёлка"));
        assertTrue(dictionary.contains("ЁЛКА"));
    }

    @Test
    void dictionarySizeAndContains() {
        WordleDictionary dictionary = new WordleDictionary(List.of("арбуз", "герой", "гонец"));
        assertEquals(3, dictionary.size());
        assertTrue(dictionary.contains("герой"));
        assertFalse(dictionary.contains("слово"));
    }

    @Test
    void getRandomWordReturnsWordFromDictionary() {
        WordleDictionary dictionary = new WordleDictionary(List.of("арбуз", "герой", "гонец"));
        String word = dictionary.getRandomWord(new Random(42));
        assertTrue(dictionary.contains(word));
    }

    @Test
    void getRandomWordOnEmptyDictionaryThrows() {
        WordleDictionary dictionary = new WordleDictionary(List.of());
        assertThrows(EmptyDictionaryException.class, () -> dictionary.getRandomWord(new Random()));
    }
}