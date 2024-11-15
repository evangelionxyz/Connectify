package tests;

import core.EncryptionUtils;

import javax.crypto.SecretKey;

public class KeyTest {
    public static void main(String[] args) throws Exception {
        EncryptionUtils.initialize();
        SecretKey key = EncryptionUtils.getGlobalSecretKey();
        String encryptedPassword = EncryptionUtils.encrypt("myPassword123", key);
        String decryptedPassword = EncryptionUtils.decrypt(encryptedPassword, key);

        System.out.println("Encrypted: "+encryptedPassword);
        System.out.println("Decrypted: "+decryptedPassword);
    }
}
