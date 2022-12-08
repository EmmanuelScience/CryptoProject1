package com.ticketswap.cryptoproject1.config;

import lombok.SneakyThrows;

import javax.crypto.Cipher;
import javax.crypto.EncryptedPrivateKeyInfo;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.security.*;
import java.security.cert.*;
import java.security.cert.Certificate;
import java.security.spec.KeySpec;
import java.util.Collections;
import java.util.List;

public class DigitalSignature {
    @SneakyThrows
    public static void main(String[] args) {
        byte[] signature = generateSignatureForMessage("C:\\chomsky\\Academics\\Fall-2022\\crypto\\CryptoProject1\\src\\A\\top.key", "Hello", "password");
        boolean verified = verifySignature("C:\\chomsky\\Academics\\Fall-2022\\crypto\\CryptoProject1\\src\\A\\top.crt", signature, "Hello".getBytes());
        byte[] certificate = readCertFromFile("C:\\chomsky\\Academics\\Fall-2022\\crypto\\CryptoProject1\\src\\A\\top.crt").getEncoded();
        boolean verified1 = verifySignature(certificate, signature, "Hello".getBytes());
        System.out.println(verified1);
    }

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
    public static CertPathValidatorResult verifyCertificateChain(List<X509Certificate> certificateChain) {
        // Create a CertificateFactory for X.509 certificates
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

        // Create a TrustAnchor for the root certificate
        X509Certificate rootCertificate = certificateChain.get(0);
        TrustAnchor trustAnchor = new TrustAnchor(rootCertificate, null);

        // Create a CertPathValidator for X.509 certificates
        CertPathValidator certPathValidator = CertPathValidator.getInstance("PKIX");

        // Create a CertPath from the certificate chain
        CertPath certPath = certificateFactory.generateCertPath(certificateChain);

        // Create a PKIXParameters object with the TrustAnchor
        PKIXParameters pkixParameters = new PKIXParameters(Collections.singleton(trustAnchor));

        // Set the revocation checking mode to be off
        pkixParameters.setRevocationEnabled(false);

        // Validate the certificate chain
        return certPathValidator.validate(certPath, pkixParameters);
    }
}
