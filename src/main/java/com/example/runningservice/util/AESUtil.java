package com.example.runningservice.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;

@Slf4j
public class AESUtil {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final int KEY_SIZE = 256;
    private static final int IV_LENGTH_BYTE = 16;

    @Value("${aes.password}")
    private String password;

    @Value("${aes.salt}")
    private String salt;

    private SecretKey getSigningKey(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec =
                new PBEKeySpec(
                        password.toCharArray(),
                        salt.getBytes(),
                        65536,
                        KEY_SIZE
                );
        return new SecretKeySpec(
                factory.generateSecret(spec).getEncoded(), ALGORITHM);
    }

    public String encrypt(String input) throws Exception {
        SecretKey secretKey = getSigningKey(password, salt);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec ivParameterSpec = generateIv();
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        byte[] cipherText = cipher.doFinal(input.getBytes());

        return  Base64.getEncoder().encodeToString(ivParameterSpec.getIV()) + "." +
                Base64.getEncoder().encodeToString(cipherText);
    }

    public String decrypt(String encryptedData) throws Exception {
        log.debug("encryptedData: {}", encryptedData);
        byte[] decodedIv =
                Base64.getDecoder().decode(
                        encryptedData.substring(0, encryptedData.indexOf(".")));

        String afterEncryptedData =
                encryptedData.substring(encryptedData.indexOf(".") + 1);

        SecretKey secretKey = getSigningKey(password, salt);
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(decodedIv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        byte[] plainText = cipher.doFinal(Base64.getDecoder()
                .decode(afterEncryptedData));

        return new String(plainText);
    }

    public static IvParameterSpec generateIv() {
        byte[] iv = new byte[IV_LENGTH_BYTE];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }
}
