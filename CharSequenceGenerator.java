package com.company;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 @brief A class with methods for optimizing iteration
 @detailed creates combinations of sequenceLength characters
 */
public class CharSequenceGenerator {

    /** length of the prefix letter combination */
    private final int sequenceLength;

    /**  the initial search boundary */
    private final char startChar;

    /**  the ultimate search boundary */
    private final char endChar;

    /**  thie object of the Sha256 Calculator class for search optimization*/
    private final Consumer<String> consumer;

    /**  string variable for the result */
    private final Predicate<String> predicate;
    
    /** 
     * Constructor - creating a new object with certain values
     * @see CharSequenceGenerator#CharSequenceGenerator(int, char, char, Consumer<String>, Predicate<String>)
     */
    public CharSequenceGenerator(int sequenceLength, char startChar, char endChar,
                                 Consumer<String> consumer, Predicate<String> predicate) {
        this.sequenceLength = sequenceLength;
        this.startChar = startChar;
        this.endChar = endChar;
        this.consumer = consumer;
        this.predicate = predicate;
    }

    /** 
     * @brief procedure for creating char buffer
     */
    public void generate() {
        char[] stringSource = new char[sequenceLength];
        Arrays.fill(stringSource, 'a');
        generateLvl(stringSource, 0);
    }

    /** 
     * @brief procedure for generation new prefix sequences
     * @param stringSource string buffer
     * @param lvl letter change level 
     */
    private void generateLvl(char[] stringSource, int lvl) {
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
