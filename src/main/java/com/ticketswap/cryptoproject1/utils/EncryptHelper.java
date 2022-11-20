package com.ticketswap.cryptoproject1.utils;

import org.springframework.util.SerializationUtils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;
import java.util.Random;

public class EncryptHelper {
    public static String generateRandomKey() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 16;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public static String encrypt(String password) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        byte[] key = password.getBytes();
        byte[] dataToSend = SerializationUtils.serialize(generateRandomKey());

        Cipher c = Cipher.getInstance("AES");
        SecretKeySpec k = new SecretKeySpec(key, "AES");
        c.init(Cipher.ENCRYPT_MODE, k);
        assert dataToSend != null;
        return Base64.getEncoder().encodeToString(c.doFinal(dataToSend));

    }

    public static String decrypt(String password, String secretKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        byte[] key = password.getBytes();
        byte[] dataToSend = secretKey.getBytes();

        Cipher c = Cipher.getInstance("AES");
        SecretKeySpec k = new SecretKeySpec(key, "AES");
        c.init(Cipher.DECRYPT_MODE, k);
        byte[] decryptedData = c.doFinal(Base64.getDecoder().decode(dataToSend));
        return Objects.requireNonNull(SerializationUtils.deserialize(decryptedData)).toString();
    }
}
