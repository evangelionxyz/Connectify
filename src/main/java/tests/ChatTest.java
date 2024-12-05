package tests;

import com.google.cloud.Timestamp;
import core.AppManager;
import models.Chat;
import models.Mahasiswa;

import java.io.IOException;

public class ChatTest {
    public static void main(String[] args) {
        try {
            AppManager.initializeFirebase();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Mahasiswa mhs = new Mahasiswa("Evan", "evangelionxyz", "Telkom University");
        Chat chat = new Chat("Hello World", Timestamp.now(), mhs);
        AppManager.storeChatToDatabase(chat);
    }
}
