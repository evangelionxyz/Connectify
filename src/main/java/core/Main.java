package core;

public class Main {
    public static void main(String[] args) {
        try {
            Application.initializeFirebase();
        } catch (Exception e) {
            System.out.printf("Exception: %s\n", e.getMessage());
        }

        new Application("Connectify").run();
    }
}
