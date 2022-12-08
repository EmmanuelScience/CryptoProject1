package com.ticketswap.cryptoproject1.utils;

import lombok.SneakyThrows;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

public class KeyPairHelper {
    private PrivateKey privateKey;
    private PublicKey  publicKey;

    @SneakyThrows
    public KeyPairHelper(){
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024);
        KeyPair pair = generator.generateKeyPair();
        privateKey = pair.getPrivate();
        publicKey = pair.getPublic();
    }

    public  String getPublicKey() {
        return encode(publicKey.getEncoded());
    }

    public  String getPrivateKey() {
        return encode(privateKey.getEncoded());
    }

    private static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
}
