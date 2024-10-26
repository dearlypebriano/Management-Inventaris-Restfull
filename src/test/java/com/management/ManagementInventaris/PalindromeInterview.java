package com.management.ManagementInventaris;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PalindromeInterview {

    private boolean isPalindromeRecursive(String value, int index) {
        if (index < value.length() / 2) {
            int firstIndex = index;
            int lastIndex = value.length() - index - 1;

            if (value.charAt(firstIndex) != value.charAt(lastIndex)) {
                return false;
            } else {
                return isPalindromeRecursive(value, index + 1);
            }
        } else {
            return true;
        }
    }

    private boolean isPalindromeIterative(String value) {
        for (int i = 0; i < value.length() / 2; i++) {
            int firstIndex = i;
            int lastIndex = value.length() - i - 1;

            if (value.charAt(firstIndex)!= value.charAt(lastIndex)) {
                return false;
            }
        }
        return true;
    }

    public boolean isPalindrome(String value) {
        return isPalindromeRecursive(value, 0);
    }

    @Test
    void test() {
        Assertions.assertTrue(isPalindrome("apa"));
    }

    @Test
    void testPalindrome() {
        Assertions.assertTrue(isPalindrome("kodok"));
        Assertions.assertTrue(isPalindrome("aba"));
        Assertions.assertTrue(isPalindrome("a"));

        Assertions.assertFalse(isPalindrome("abab"));
        Assertions.assertFalse(isPalindrome("kodcok"));
        Assertions.assertFalse(isPalindrome("eko"));
    }
}