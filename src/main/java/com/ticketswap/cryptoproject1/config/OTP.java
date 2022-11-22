package com.ticketswap.cryptoproject1.config;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class OTP {

    private final SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");

    public OTP() throws NoSuchAlgorithmException {
    }

    public String generate(int maxLength) {
        final StringBuilder otp = new StringBuilder(maxLength);
        for (int i = 0; i < maxLength; i++) {
            otp.append(secureRandom.nextInt(9));
        }
        return otp.toString();
    }
}