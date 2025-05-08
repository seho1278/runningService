package com.example.runningservice.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AESUtilTest {
    private AESUtil aesUtil;

    @BeforeEach
    public void setUp() {
        aesUtil = new AESUtil();
        aesUtil.setPassword("password");
        aesUtil.setSalt("salt");
    }

    @Test
    public void testEncryptDecrypt() throws Exception {
        String originalText = "TestString123";

        // 암호화
        String encryptedText = aesUtil.encrypt(originalText);
        assertNotNull(encryptedText);

        // 복호화
        String decryptedText = aesUtil.decrypt(encryptedText);
        assertEquals(originalText, decryptedText);
    }

    @Test
    void repeatableTest () throws Exception {
        //given
        String plainText = "TestString123";
        String firstEncryptedText = aesUtil.encrypt(plainText);
        String secondEncryptedText = aesUtil.encrypt(plainText);

        //then
        assertTrue(firstEncryptedText != secondEncryptedText);
    }
}