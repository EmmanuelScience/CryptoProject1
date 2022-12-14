package com.ticketswap.cryptoproject1.crypto;

import com.ticketswap.cryptoproject1.AppSession;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.SerializationUtils;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Configuration
@Converter
public class Encryptor implements AttributeConverter<Object, String>{
    @Value("${encryption.key}")
    private String keyEncrypt;
    private final String cipherEncrypt = "AES";

    private  Key key;
    private  Cipher cipher;

    public Encryptor() {
        this.keyEncrypt  = AppSession.keyPassword;
    }

    private  Key getKey() {
        if (key == null) key = new SecretKeySpec(keyEncrypt.getBytes(), cipherEncrypt);
        return key;
    }

    private   Cipher getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        if (cipher == null) cipher = Cipher.getInstance(cipherEncrypt);
        return cipher;
    }

    private void cipherInit(int mode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        getCipher().init(mode, getKey());
    }

    @SneakyThrows
    @Override
    public String convertToDatabaseColumn(Object attribute) {
        if (attribute == null)  return null;
        cipherInit(Cipher.ENCRYPT_MODE);
        byte[] bytes = SerializationUtils.serialize(attribute);
        assert bytes != null;
        String encrypted = Base64.getEncoder().encodeToString(getCipher().doFinal(bytes));
        System.out.println("Encrypted: " + encrypted + "\nlength: " + encrypted.length());
        return encrypted;
    }

    @SneakyThrows
    @Override
    public Object convertToEntityAttribute(String dbData) {
        if (dbData == null)return null;
        System.out.println("Decrypted: " + dbData + "\nlength: " + dbData.length());
        cipherInit(Cipher.DECRYPT_MODE);
        byte[] bytes = getCipher().doFinal(Base64.getDecoder().decode(dbData));
        return SerializationUtils.deserialize(bytes);
    }
}
