package com.management.ManagementInventaris.filter;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Utility class for detecting and filtering toxic words in text.
 * Supports custom and default toxic words with flexible replacement options.
 */
public class ToxicWordFilter {

    private static final Map<String, String> DEFAULT_TOXIC_WORDS_MAP = new HashMap<>();

    static {
        DEFAULT_TOXIC_WORDS_MAP.put("fuck", "f**k");
        DEFAULT_TOXIC_WORDS_MAP.put("shit", "s**t");
        DEFAULT_TOXIC_WORDS_MAP.put("cunt", "c**t");
        DEFAULT_TOXIC_WORDS_MAP.put("ass", "a**s");
        DEFAULT_TOXIC_WORDS_MAP.put("bitch", "b**tch");
        DEFAULT_TOXIC_WORDS_MAP.put("dick", "d**ck");
        DEFAULT_TOXIC_WORDS_MAP.put("pussy", "p**sy");
        DEFAULT_TOXIC_WORDS_MAP.put("prick", "p**rk");
        DEFAULT_TOXIC_WORDS_MAP.put("tits", "t**ts");
        DEFAULT_TOXIC_WORDS_MAP.put("dickhead", "d**ckhead");
        DEFAULT_TOXIC_WORDS_MAP.put("slut", "s**lut");
        DEFAULT_TOXIC_WORDS_MAP.put("asshole", "a**shole");
        DEFAULT_TOXIC_WORDS_MAP.put("kontol", "k****l");
        DEFAULT_TOXIC_WORDS_MAP.put("jancok", "j****k");
        DEFAULT_TOXIC_WORDS_MAP.put("ngentot", "n****t");
        DEFAULT_TOXIC_WORDS_MAP.put("memek", "m***k");
        DEFAULT_TOXIC_WORDS_MAP.put("ngontol", "n*****l");
    }

    /**
     * Filters toxic words in the given text based on default and custom toxic words.
     *
     * @param text The text to be filtered.
     * @param customWords An array of custom toxic words to be filtered.
     * @param replacement The string to replace toxic words with.
     * @return The filtered text.
     */
    public static String filterToxic(String text, String[] customWords, String replacement) {
        Map<String, String> toxicWordsMap = new HashMap<>(DEFAULT_TOXIC_WORDS_MAP);

        for (String word : customWords) {
            toxicWordsMap.put(word.toLowerCase(), getReplacement(word, replacement));
        }

        for (Map.Entry<String, String> entry : toxicWordsMap.entrySet()) {
            text = text.replaceAll("(?i)" + Pattern.quote(entry.getKey()), entry.getValue());
        }

        return text;
    }

    /**
     * Checks if the given text contains any toxic words.
     *
     * @param text The text to check for toxic words.
     * @param customWords An array of custom toxic words to be checked.
     * @return True if the text contains toxic words, otherwise false.
     */
    public static boolean containsToxicWords(String text, String[] customWords) {
        Map<String, String> toxicWordsMap = new HashMap<>(DEFAULT_TOXIC_WORDS_MAP);

        for (String word : customWords) {
            toxicWordsMap.put(word.toLowerCase(), "");
        }

        for (String toxicWord : toxicWordsMap.keySet()) {
            if (Pattern.compile("(?i)" + Pattern.quote(toxicWord)).matcher(text).find()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generates a replacement string for a given toxic word.
     *
     * @param word The toxic word to be replaced.
     * @param replacement The replacement pattern to use.
     * @return The replacement string.
     */
    private static String getReplacement(String word, String replacement) {
        if (replacement != null && !replacement.isEmpty()) {
            return replacement;
        }

        int length = word.length();
        if (length <= 1) {
            return word;
        }
        StringBuilder replaced = new StringBuilder();
        replaced.append(word.charAt(0));
        for (int i = 1; i < length - 1; i++) {
            replaced.append('*');
        }
        replaced.append(word.charAt(length - 1));
        return replaced.toString();
    }
}