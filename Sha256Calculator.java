package com.company;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.function.Consumer;

public class Sha256Calculator implements Runnable {

    private final String prefix;
    private final int suffixLength;
    private final char startChar;
    private final char endChar;
    private final String resultHash;
    private final Consumer<String> sourceStringHandler;

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
