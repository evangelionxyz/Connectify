package core;

import imgui.ImGui;

public class Application {
    private final Window window;
    public Application(String title) {
        window = new Window(1080, 720, title);
        System.out.println("Application created");
    }

    public void run() {
        System.out.println("Application is running");

        window.setGuiRenderFunction(()-> {
            ImGui.begin("Test Window");
            ImGui.text("Hello World");
            ImGui.end();

            ImGui.showDemoWindow();
        });

        while (window.isLooping()) {
            window.pollEvents();
            window.swapBuffer();
        }
        window.destroy();
    }
}
