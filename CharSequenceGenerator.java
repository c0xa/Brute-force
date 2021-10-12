package com.company;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class CharSequenceGenerator {
    private final int sequenceLength;
    private final char startChar;
    private final char endChar;
    private final Consumer<String> consumer;
    private final Predicate<String> predicate;

    public CharSequenceGenerator(int sequenceLength, char startChar, char endChar,
                                 Consumer<String> consumer, Predicate<String> predicate) {
        this.sequenceLength = sequenceLength;
        this.startChar = startChar;
        this.endChar = endChar;
        this.consumer = consumer;
        this.predicate = predicate;
    }

    public void generate() {
        char[] stringSource = new char[sequenceLength];
        Arrays.fill(stringSource, 'a');
        generateLvl(stringSource, 0);
    }

    private void generateLvl(char[] stringSource, int lvl) {
//        for (int i = 0; i < sequenceLength; i++)
//            System.out.print(stringSource[i]);
//        System.out.println();
        if (lvl < sequenceLength) {
            for (char ch = startChar; ch <= endChar; ch++) {
                stringSource[lvl] = ch;
                generateLvl(stringSource, lvl + 1);
            }
        } else {
            String finalString = new String(stringSource);
            if (predicate.test(finalString)) {
                consumer.accept(finalString);
            }
        }
    }
}
