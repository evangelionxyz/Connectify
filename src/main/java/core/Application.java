package core;
import imgui.ImGui;
import views.CommunityWindow;
import views.QuestWindow;
import views.HRDWindow;
import views.LoginWindow;

public class Application {
    private final Window window;
    private final LoginWindow loginWindow = new LoginWindow();
    private final CommunityWindow communityWindow = new CommunityWindow();
    private final HRDWindow hrdWindow = new HRDWindow();
    private final QuestWindow questWindow = new QuestWindow();

    public Application(String title) {
        window = new Window(1080, 640, title);
        System.out.println("Application created");
        loginWindow.init();
        communityWindow.init();
        hrdWindow.init();
        questWindow.init();
    }

    private void toolBar() {
        ImGui.begin("Tool Bar");
        if (ImGui.button("Exit")) {
            window.close();
        }
        ImGui.sameLine();
        if (ImGui.button("Logout")) {
            loginWindow.open();
            AppManager.currentUser = null;
        }

        ImGui.sameLine();
        if (AppManager.currentUser != null) {
            ImGui.text(String.format("%s - %s", AppManager.currentUser.getName(),
                    AppManager.currentUser.getCompany()));
        }

        ImGui.sameLine();
        ImGui.end();
    }

    public void run() {
        System.out.println("Application is running");

        window.setGuiRenderFunction(()-> {
            loginWindow.render();

            toolBar();
            if (AppManager.currentUser != null) {
                communityWindow.render();
                if (AppManager.currentUser.isHRD()) {
                    hrdWindow.render();
                }
                else {
                    questWindow.render();
                }
            }
            ImGui.showDemoWindow();
        });

        while (window.isLooping()) {
            window.pollEvents();
            window.swapBuffer();
        }
        window.destroy();
    }
}
