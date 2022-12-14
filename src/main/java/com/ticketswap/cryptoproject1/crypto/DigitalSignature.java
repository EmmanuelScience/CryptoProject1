package com.ticketswap.cryptoproject1.crypto;

import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DigitalSignature {

    public static byte[] generateSignatureForMessage(String privateKeyPath, String message, String password) throws Exception {
        PrivateKey privateKey = readKeyFromFile(privateKeyPath, password);
        Signature s = Signature.getInstance("SHA256withRSA");
        s.initSign(privateKey);
        s.update(ByteBuffer.wrap(message.getBytes()));
        return s.sign();
    }

    @SneakyThrows
    public static boolean verifySignature(String certPath, byte[] signature, byte[] signedContent) {
        Certificate cert = readCertFromFile(certPath);
        Signature s = Signature.getInstance("SHA256withRSA");
        s.initVerify(cert);
        s.update(signedContent);
        return s.verify(signature);
    }

    @SneakyThrows
    public static boolean verifySignature(byte[] certByte, byte[] signature, byte[] signedContent) {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        Certificate cert = cf.generateCertificate(new ByteArrayInputStream(certByte));
        Signature s = Signature.getInstance("SHA256withRSA");
        s.initVerify(cert);
        s.update(signedContent);
        return s.verify(signature);
    }

    @SneakyThrows
    public static X509Certificate readCertFromFile(String path) {
        CertificateFactory fac = CertificateFactory.getInstance("X509");
        FileInputStream is = new FileInputStream(path);
        return (X509Certificate) fac.generateCertificate(is);
    }

    @SneakyThrows
    public static PrivateKey  readKeyFromFile(String path, String password) {
        File f = new File(path);
        FileInputStream fis = new FileInputStream(f);
        DataInputStream dis = new DataInputStream(fis);
        byte[] keyBytes = new byte[(int)f.length()];
        dis.readFully(keyBytes);
        dis.close();
        EncryptedPrivateKeyInfo encryptPKInfo = new EncryptedPrivateKeyInfo(keyBytes);
        Cipher cipher = Cipher.getInstance(encryptPKInfo.getAlgName());
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray());
        SecretKeyFactory secFac = SecretKeyFactory.getInstance(encryptPKInfo.getAlgName());
        Key pbeKey = secFac.generateSecret(pbeKeySpec);
        AlgorithmParameters algParams = encryptPKInfo.getAlgParameters();
        cipher.init(Cipher.DECRYPT_MODE, pbeKey, algParams);
        KeySpec pkcs8KeySpec = encryptPKInfo.getKeySpec(cipher);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(pkcs8KeySpec);
    }

    @SneakyThrows
    public static void verifyChain(List<X509Certificate> chain){
        // Loop over the certificates in the chain
        for (int i = 0; i < chain.size(); i++) {
            X509Certificate cert = (X509Certificate) chain.get(i);
            // check if the certificate was/is valid
            cert.checkValidity();
            // check if the previous certificate was issued by this certificate
            if (i > 0)
                chain.get(i - 1).verify(chain.get(i).getPublicKey());
        }
        System.out.println("All certificates are valid");
    }

}
