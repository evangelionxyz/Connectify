package core;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

public class EncryptionUtils {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";
    private static final String KEY_FILE_PATH = "C:/connectify-telu-aes-key.txt";

    private static SecretKey globalSecretKey;

    public static SecretKey getGlobalSecretKey() {
        return globalSecretKey;
    }

    public static void initialize() {
        try {
            String base64Key = loadKeyFromFile();
            byte[] decodedKey = Base64.getDecoder().decode(base64Key);
            globalSecretKey = new SecretKeySpec(decodedKey, ALGORITHM);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String loadKeyFromFile() throws IOException {
        File file = new File(EncryptionUtils.KEY_FILE_PATH);
        return Files.readString(file.toPath());
    }

    public static String encrypt(String password, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(password.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static String decrypt(String encryptedPassword, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decryptBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword.getBytes()));
        return new String(decryptBytes);
    }
}
