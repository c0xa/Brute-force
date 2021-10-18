package com.company;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;

/**
 @brief The class that implements the Runnable interface
 @detailed Iterates through combinations and calculates sha
 */
public class Sha256Calculator implements Runnable {

    /**  prefix */
    private final String prefix;
    
    /**  suffix length*/
    private final int suffixLength;
    
    /**  the initial search boundary */
    private final char startChar;

    /**  the ultimate search boundary */
    private final char endChar;
    
    /**  result sha calculate*/
    private final String resultHash;

    /**  the initial search boundary */
    private final Consumer<String> sourceStringHandler;

    /** 
     * Constructor - creating a new object with certain values
     * @see CharSequenceGenerator#CharSequenceGenerator(String, int, char, char, String, Consumer<String>)
     */
    public Sha256Calculator(String prefix, int suffixLength,
                            char startChar, char endChar,
                            String resultHash, Consumer<String> sourceStringHandler) {
        this.prefix = prefix;
        this.suffixLength = suffixLength;
        this.startChar = startChar;
        this.endChar = endChar;
        this.resultHash = resultHash;
        this.sourceStringHandler = sourceStringHandler;
    }

    /**
     * @brief The implementation of the run method
     * @detailed creates combinations of full lenght characters
     */
    @Override
    public void run() {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.getMessage());
            throw new IllegalStateException(ex);
        }
        char[] stringSource = new char[prefix.length() + suffixLength];
        prefix.getChars(0, prefix.length(), stringSource, 0);
        calculateHashesLvl(stringSource, digest, prefix.length(), 0);
    }

    /**
     * @brief procedure for generation new sequences
     * @detailed find SHA result for char sequence
     * @param stringSource string buffer
     * @param digest MessageDigest
     * @param offset prefix length
     * @param lvl letter change level 
     */
    private void calculateHashesLvl(char[] stringSource, MessageDigest digest, int offset, int lvl) {
        if (lvl < suffixLength) {
            for (char ch = startChar; ch <= endChar; ch++) {
                stringSource[offset + lvl] = ch;
                calculateHashesLvl(stringSource, digest, offset, lvl + 1);
            }
        } else {
            String str = new String(stringSource);
            byte[] byteArray = str.getBytes();
            digest.update(byteArray, 0, byteArray.length);
            byte[] digestBytes = digest.digest();
            digest.reset();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < digestBytes.length; i++)
            {
                sb.append(Integer.toString((digestBytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            if (resultHash.equals(sb.toString())) {
                sourceStringHandler.accept(str);
            }
        }
    }
}
