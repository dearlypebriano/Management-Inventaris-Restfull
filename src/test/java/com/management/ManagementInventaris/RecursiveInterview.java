package com.management.ManagementInventaris;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RecursiveInterview {

    @Test
    void testFactorialRecursive() {
        int result = factorialRecursive(5);
        Assertions.assertEquals(120, result);
    }

    int factorialRecursive(int value) {
        if (value == 1) {
            return 1;
        } else {
            return value * factorialRecursive(value - 1);
        }
    }
}