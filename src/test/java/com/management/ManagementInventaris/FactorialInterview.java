package com.management.ManagementInventaris;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FactorialInterview {

    int faktorial(int value) {
        if (value <= 0) {
            return 1;
        }
        int result = 1;
        for (int i = value; i >= 1; i--) {
            result *= i;
        }
        return result;
    }

    int factorialRecursive(int value) {
        if (value <= 0) {
            return 1;
        } else {
            return value * factorialRecursive(value - 1);
        }
    }

    int factorialTailRecursive(int total, int value) {
        if (value <= 0) {
            return total;
        } else {
            return factorialTailRecursive(total * value, value - 1);
        }
    }

    @Test
    void testFaktorial() {
        Assertions.assertEquals(1, faktorial(0));
        Assertions.assertEquals(1, faktorial(1));
        Assertions.assertEquals(2, faktorial(2));
        Assertions.assertEquals(6, faktorial(3));
        Assertions.assertEquals(24, faktorial(4));
        Assertions.assertEquals(120, faktorial(5));
    }

    @Test
    void testFaktorialRecursive() {
        Assertions.assertEquals(1, factorialRecursive(0));
        Assertions.assertEquals(1, factorialRecursive(1));
        Assertions.assertEquals(2, factorialRecursive(2));
        Assertions.assertEquals(6, factorialRecursive(3));
        Assertions.assertEquals(24, factorialRecursive(4));
        Assertions.assertEquals(120, factorialRecursive(5));
    }

    @Test
    void testFaktoriaTaillRecursive() {
        Assertions.assertEquals(1, factorialTailRecursive(1, 0));
        Assertions.assertEquals(1, factorialTailRecursive(1, 1));
        Assertions.assertEquals(2, factorialTailRecursive(1,2));
        Assertions.assertEquals(6, factorialTailRecursive(1, 3));
        Assertions.assertEquals(24, factorialTailRecursive(1,4));
        Assertions.assertEquals(120, factorialTailRecursive(1,5));
    }
}