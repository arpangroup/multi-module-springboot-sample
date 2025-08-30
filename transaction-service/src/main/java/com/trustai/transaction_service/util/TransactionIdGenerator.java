package com.trustai.transaction_service.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;

//@Component
public class TransactionIdGenerator {

    private static final String PREFIX = "TRX";
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int RANDOM_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    public static String generateTransactionId() {
        StringBuilder sb = new StringBuilder(PREFIX);

        // Add timestamp
        String timestamp = new SimpleDateFormat("yyMMddHHmmss").format(new Date()); // e.g. 250617142530
        sb.append(timestamp);

        // Add random suffix
        for (int i = 0; i < RANDOM_LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }

        return sb.toString();
    }
}
