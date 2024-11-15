package core;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Base64;

public class KeyManager {
    private static final String GEN_FILE_PATH = "D:/connectify_telu_aes_key.txt";

    public static void main(String[] args) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);

            SecretKey secretKey = keyGen.generateKey();

            String base64Key = Base64.getEncoder().encodeToString(secretKey.getEncoded());

            saveKeyToFile(base64Key);
            System.out.println("Key successfully saved to "+ GEN_FILE_PATH);

        } catch (Exception e) {
            System.err.println("[ERROR] Key Manager: "+e.getMessage());
            e.printStackTrace();
        }
    }

    private static void saveKeyToFile(String base64Key) throws IOException {
        File file = new File(KeyManager.GEN_FILE_PATH);
        try (FileWriter writer = new FileWriter(file)){
            writer.write(base64Key);
        }
    }
}
