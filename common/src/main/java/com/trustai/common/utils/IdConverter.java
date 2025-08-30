package com.trustai.common.utils;

import java.security.SecureRandom;

/**
 * We are using a Base36 encoding:
 *      0–9 (10 digits)
 *      A–Z (26 uppercase letters)
 *      → Total: 36 characters
 *
 * With 9 characters in Base36, the maximum ID you can encode is:
 *      36^9 = 1,015,599,162,777,600
 *
 * That’s up to 1 quadrillion+ — safe for most use cases involving long.
 */
public class IdConverter {
    private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = ALPHABET.length(); // 36
    private static final int TARGET_LENGTH = 9;
    private static final long MAX_ID = (long) Math.pow(BASE, TARGET_LENGTH) - 1;
    private static final long SECRET = 987654321L;
    private static final SecureRandom random = new SecureRandom();

    public static String encode(long id) {
        if (id < 0 || id > MAX_ID) {
            throw new IllegalArgumentException("ID must be between 0 and " + MAX_ID);
        }

        long obfuscated = id ^ SECRET;

        StringBuilder sb = new StringBuilder();
        do {
            sb.insert(0, ALPHABET.charAt((int) (obfuscated % BASE)));
            obfuscated /= BASE;
        } while (obfuscated > 0);

        // Pad with trailing zeros to reach the target length
        while (sb.length() < TARGET_LENGTH) {
            sb.append('0');  // trailing zero padding
        }

        // If the first character is '0' (very unlikely), replace with 'A'
        if (sb.charAt(0) == '0') {
            sb.setCharAt(0, 'A');
        }

        return sb.toString();
    }

    public static long decode(String str) {
        if (str.length() != TARGET_LENGTH) {
            throw new IllegalArgumentException("Encoded string must be exactly " + TARGET_LENGTH + " characters.");
        }

        // Remove trailing zeros (padding)
        String trimmed = str.replaceAll("0+$", "");
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Encoded string is invalid after removing padding.");
        }

        long obfuscated = 0;
        for (char c : trimmed.toCharArray()) {
            int index = ALPHABET.indexOf(c);
            if (index == -1) {
                throw new IllegalArgumentException("Invalid character in encoded string: " + c);
            }
            obfuscated = obfuscated * BASE + index;
        }

        return obfuscated ^ SECRET;
    }


    /*public static void main(String[] args) {
        for (long id = 1; id <= 5; id++) {
            String encoded = IdConverter.encode(id);
            long decoded = IdConverter.decode(encoded);
            System.out.printf("ID: %-5d → Encoded: %s → Decoded: %d%n", id, encoded, decoded);
        }
    }*/
}
