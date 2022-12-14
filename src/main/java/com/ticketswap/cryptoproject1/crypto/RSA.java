package com.ticketswap.cryptoproject1.crypto;

import lombok.SneakyThrows;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;


public class RSA {

    private PrivateKey privateKey;

    private String peerPublicKeyString;
    private String privateKeyString;
    private String publicKeyString;

    public void setPeerPublicKey(String peerPublicKey) {
        this.peerPublicKeyString = peerPublicKey;
    }

    public String getPublicKeyString() {
        return publicKeyString;
    }

    public String getPrivateKeyString() {
        return privateKeyString;
    }

    public void setPrivateKeyString(String privateKeyString) {
        this.privateKeyString = privateKeyString;
    }

    public void setPublicKeyString(String publicKeyString) {
        this.publicKeyString = publicKeyString;
    }


    public void printKeys(){
        System.err.println("Public key\n"+ peerPublicKeyString);
        System.err.println("Private key\n"+ privateKeyString);
    }

    public String encrypt(String message) throws Exception {
        X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(decode(peerPublicKeyString));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey peerPublicKey = keyFactory.generatePublic(keySpecPublic);
        byte[] messageToBytes = message.getBytes();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        System.out.println("the key used is "+peerPublicKeyString);
        cipher.init(Cipher.ENCRYPT_MODE, peerPublicKey);
        byte[] encryptedBytes = cipher.doFinal(messageToBytes);
        return encode(encryptedBytes);
    }

    private static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }
    private static byte[] decode(String data) {
        data = data.strip();
        return Base64.getDecoder().decode(data);
    }


    public String decrypt(String encryptedMessage) throws Exception {
        PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(decode(privateKeyString));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        privateKey = keyFactory.generatePrivate(keySpecPrivate);
        byte[] encryptedBytes = decode(encryptedMessage);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] decryptedMessage = cipher.doFinal(encryptedBytes);
        return new String(decryptedMessage, StandardCharsets.UTF_8);
    }

    public void generateKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(1024);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();
        privateKeyString = encode(privateKey.getEncoded());
        publicKeyString = encode(publicKey.getEncoded());
    }

}
