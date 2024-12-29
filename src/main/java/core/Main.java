package core;

public class Main {
    public static void main(String[] args) {
        try {
            EncryptionUtils.initialize();
            AppManager.initializeFirebase();
        } catch (Exception e) {
            System.err.printf("Exception: %s\n", e.getMessage());
        }
        new Application("Connectify").run();
    }
}
