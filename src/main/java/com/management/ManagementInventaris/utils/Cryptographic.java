package com.management.ManagementInventaris.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Base64;

public final class Cryptographic {

    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "inventarisSecret";
    private static final String INSTANCE = "PBKDF2WithHmacSHA512";
    private static final String FINAL_SALT = "inventarisSalt";
    private static final int KEY_LENGTH = 256;
    private static final int ITERATION_COUNT = 65536;
    private static final int REQUIRED_KEY_LENGTH = 16;

    public static String encrypt(String data) throws GeneralSecurityException {
        return encrypt(data.getBytes(StandardCharsets.UTF_8));
    }

    public static String encrypt(int data) throws GeneralSecurityException {
        return encrypt(Integer.toString(data));
    }

    public static String encrypt(long data) throws GeneralSecurityException {
        return encrypt(Long.toString(data));
    }

    public static String encrypt(double data) throws GeneralSecurityException {
        return encrypt(Double.toString(data));
    }

    /**
     * Encrypts the given data using AES algorithm.
     *
     * @param data The data to encrypt.
     * @return The encrypted data as a Base64 encoded string.
     */
    private static String encrypt(byte[] data) {
        validateKey();
        try {
            SecureRandom secureRandom = new SecureRandom();
            byte[] iv = new byte[16];
            secureRandom.nextBytes(iv);
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance(INSTANCE);
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), FINAL_SALT.getBytes(), ITERATION_COUNT, KEY_LENGTH);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);

            byte[] cipherText = cipher.doFinal(data);
            byte[] encryptedData = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, encryptedData, 0, iv.length);
            System.arraycopy(cipherText, 0, encryptedData, iv.length, cipherText.length);

            return Base64.getUrlEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String strToDecrypt) throws GeneralSecurityException {
        byte[] decryptedData = decrypt(Base64.getUrlDecoder().decode(strToDecrypt));
        assert decryptedData != null;
        return new String(decryptedData, StandardCharsets.UTF_8);
    }

    public static int decryptInt(String strToDecrypt) throws GeneralSecurityException {
        String decrypted = decrypt(strToDecrypt);
        return Integer.parseInt(decrypted);
    }

    public static long decryptLong(String strToDecrypt) throws GeneralSecurityException {
        String decrypted = decrypt(strToDecrypt);
        return Long.parseLong(decrypted);
    }

    public static byte[] decryptBytes(String strToDecrypt) throws GeneralSecurityException {
        return decrypt(Base64.getUrlDecoder().decode(strToDecrypt));
    }

    public static double decryptDouble(String strToDecrypt) throws GeneralSecurityException {
        String decrypted = decrypt(strToDecrypt);
        return Double.parseDouble(decrypted);
    }

    /**
     * Decrypts the given encrypted data using AES algorithm.
     *
     * @param encryptedData The Base64 encoded encrypted data.
     * @return The decrypted data as a string.
     */
    private static byte[] decrypt(byte[] encryptedData) {
        validateKey();
        try {
            byte[] iv = new byte[16];
            System.arraycopy(encryptedData, 0, iv, 0, iv.length);
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            SecretKeyFactory factory = SecretKeyFactory.getInstance(INSTANCE);
            KeySpec spec = new PBEKeySpec(SECRET_KEY.toCharArray(), FINAL_SALT.getBytes(), ITERATION_COUNT, KEY_LENGTH);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKeySpec = new SecretKeySpec(tmp.getEncoded(), ALGORITHM);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivspec);

            byte[] cipherText = new byte[encryptedData.length - iv.length];
            System.arraycopy(encryptedData, iv.length, cipherText, 0, cipherText.length);

            return cipher.doFinal(cipherText);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void validateKey() {
        if (Cryptographic.SECRET_KEY.length() != REQUIRED_KEY_LENGTH) throw new IllegalArgumentException("Key must be 16 characters long.");
    }
}