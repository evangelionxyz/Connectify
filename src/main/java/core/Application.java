package core;

public class Application {
    private final Window window;
    public Application(String title) {
        window = new Window(420, 640, title);
        System.out.println("Application created");
    }

    public void run() {
        System.out.println("Application is running");
        while (window.isLooping()) {
            window.pollEvents();
            window.swapBuffer();
        }
    }
}
