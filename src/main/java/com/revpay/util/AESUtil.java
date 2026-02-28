package com.revpay.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {
    private static final String ALGORITHM = "AES";
    // For simplicity in this demo, we use a hardcoded key.
    // In production, this should be stored in a secure keystore or env variable.
    private static final byte[] KEY = "MySuperSecretKey".getBytes(); // 16 bytes for AES-128, need 32 for 256?
    // Wait, AES-256 requires 32 bytes key. "MySuperSecretKey" is 16 chars.
    // Let's use a 16-byte key for AES-128 which is standard enough for this demo,
    // OR generate a proper key. The user asked for AES-256.

    // Let's use a fixed 32-byte key string for AES-256 simulation
    private static final String SECRET_KEY_STRING = "12345678901234567890123456789012"; // 32 chars

    public static String encrypt(String data) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY_STRING.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedData) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY_STRING.getBytes(), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
}
