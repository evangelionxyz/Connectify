package core;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public class Application {
    private final Window window;
    public Application(String title) {
        window = new Window(420, 640, title);
        System.out.println("Application created");
    }

    public void run() {
        System.out.println("Application is running");
        window.setGuiRenderFunction(()-> {
            ImGui.showDemoWindow();
            int windowFlags = ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoDocking;
            ImGui.begin("Test Window", windowFlags);
            ImGui.text("Hello World");
            ImGui.end();
        });
        while (window.isLooping()) {
            window.pollEvents();
            window.swapBuffer();
        }
        window.destroy();
    }
}
