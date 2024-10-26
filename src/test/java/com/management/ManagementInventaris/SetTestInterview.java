package com.management.ManagementInventaris;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SetTestInterview {

    public static class Set {

        private int size = 0;

        private String[] array = new String[10];


        public boolean add(String value) {
            if (contains(value)) {
                return false;
            } else {
                ensureCapactiy();
                array[size] = value;
                size++;
                return true;
            }
        }

        private void ensureCapactiy() {
            if (size >= array.length) {
                // membuat ulang / resize
                String[] temporary = new String[array.length * 2];
                // copy data dari array ke temp
                for (int i = 0; i < array.length; i++) {
                    temporary[i] = array[i];
                }
                array = temporary;
            }
        }

        public boolean contains(String value) {
            for (String item : array) {
                if (value.equals(item)) {
                    return true;
                }
            }
            return false;
        }

        public int size() {
            return size;
        }

        private int indexOf(String value) {
            for (int i = 0; i < array.length; i++) {
                if (value.equals(array[i])) {
                    return i;
                }
            }
            return -1;
        }

        public boolean remove(String value) {
            if (contains(value)) {
                int indexRemoved = indexOf(value);
                for (int i = indexRemoved; i <= size; i++) {
                    array[i] = array[i + 1];
                }
                size--;
                return true;
            } else {
                return false;
            }
        }
    }

    @Test
    void testAdd() {
        Set set = new Set();
        Assertions.assertTrue(set.add("Dearly"));
        Assertions.assertFalse(set.add("Dearly"));
        Assertions.assertTrue(set.add("Febriano"));
        Assertions.assertFalse(set.add("Febriano"));
    }

    @Test
    void testContains() {
        Set set = new Set();
        set.add("Dearly");
        set.add("Febriano");

        Assertions.assertTrue(set.contains("Dearly"));
        Assertions.assertTrue(set.contains("Febriano"));
        Assertions.assertFalse(set.contains("Irwansyah "));
    }

    @Test
    void testSize() {
        Set set = new Set();
        Assertions.assertEquals(0, set.size());

        set.add("Dearly");
        Assertions.assertEquals(1, set.size());

        set.add("Dearly");
        Assertions.assertEquals(1, set.size());

        set.add("Febriano");
        Assertions.assertEquals(2, set.size());
    }

    @Test
    void testRemove() {
        Set set = new Set();
        set.add("Dearly");
        set.add("Febriano");
        set.add("Irwansyah");
        set.add("Arvan");
        set.add("Riani");

        Assertions.assertEquals(5, set.size());

        set.remove("Febriano");

        Assertions.assertEquals(4, set.size());

        Assertions.assertTrue(set.contains("Dearly"));
        Assertions.assertTrue(set.contains("Irwansyah"));
        Assertions.assertTrue(set.contains("Arvan"));
        Assertions.assertTrue(set.contains("Riani"));
        Assertions.assertFalse(set.contains("Febriano"));
    }

    @Test
    void testGrowth() {
        Set set = new Set();
        for (int i = 0; i < 100; i++) {
            set.add("Item-" + i);
        }

        Assertions.assertEquals(100, set.size());
    }
}