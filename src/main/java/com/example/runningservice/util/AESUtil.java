package com.example.runningservice.util;

import com.example.runningservice.exception.CustomException;
import com.example.runningservice.exception.ErrorCode;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Setter
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

    public String decrypt(String encryptedData) {
        SecretKey secretKey = null;
        try {
            log.debug("encryptedData: {}", encryptedData);

            // Base64 디코딩 및 문자열 조작
            int separatorIndex = encryptedData.indexOf(".");
            if (separatorIndex == -1) {
                throw new IllegalArgumentException("Invalid encrypted data format.");
            }

            byte[] decodedIv = Base64.getDecoder().decode(encryptedData.substring(0, separatorIndex));
            String afterEncryptedData = encryptedData.substring(separatorIndex + 1);

            // 키 및 Cipher 초기화
            try {
                secretKey = getSigningKey(password, salt);
            } catch (Exception e) {
                log.error("Failed to obtain signing key", e);
                throw new CustomException(ErrorCode.DECRYPTION_ERROR);
            }

            if (secretKey == null) {
                throw new CustomException(ErrorCode.DECRYPTION_ERROR); // secretKey가 null인 경우 처리
            }

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(decodedIv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

            // 복호화 및 문자열 변환
            byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(afterEncryptedData));
            return new String(plainText, StandardCharsets.UTF_8);  // UTF-8로 문자열 변환

        } catch (IllegalArgumentException | IllegalBlockSizeException | BadPaddingException |
                 NoSuchAlgorithmException | NoSuchPaddingException |
                 InvalidAlgorithmParameterException |
                 InvalidKeyException e) {
            log.error("Decryption failed", e);
            throw new CustomException(ErrorCode.DECRYPTION_ERROR);
        } catch (Exception e) {
            // getSigningKey에서 발생하지 않는 다른 예외 처리
            log.error("Unexpected error during decryption", e);
            throw new CustomException(ErrorCode.DECRYPTION_ERROR);
        }
    }

    private static IvParameterSpec generateIv() {
        byte[] iv = new byte[IV_LENGTH_BYTE];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    public String generateHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
