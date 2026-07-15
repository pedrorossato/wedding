package com.pedrogio.wedding.auth;

import com.pedrogio.wedding.config.WeddingProperties;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

@Service
public class EncryptionService {

    private final SecretKeySpec keySpec;

    public EncryptionService(WeddingProperties properties) {
        byte[] keyBytes = sha256(properties.getLoginEncryption().getKey().getBytes(StandardCharsets.UTF_8));
        this.keySpec = new SecretKeySpec(keyBytes, "AES");
    }

    public String decrypt(String encryptedBase64) {
        try {
            byte[] data = Base64.getDecoder().decode(encryptedBase64);
            byte[] iv = Arrays.copyOfRange(data, 0, 12);
            byte[] ciphertext = Arrays.copyOfRange(data, 12, data.length);

            GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

            byte[] plaintext = cipher.doFinal(ciphertext);
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt password", e);
        }
    }

    private byte[] sha256(byte[] input) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(input);
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
