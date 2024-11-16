package core;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import imgui.ImGui;
import views.CommunityWindow;
import views.HRDWindow;
import views.LoginWindow;
import java.util.*;

public class Application {

    private final Window window;
    private final LoginWindow loginWindow = new LoginWindow();
    private final CommunityWindow communityWindow = new CommunityWindow();
    private final HRDWindow hrdWindow = new HRDWindow();

    public Application(String title) {
        window = new Window(1080, 720, title);
        System.out.println("Application created");
        loginWindow.init();
        communityWindow.init();
        hrdWindow.init();
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

    public static void readMessageFromFirestore() {
        try {
            ApiFuture<QuerySnapshot> future = AppManager.firestore.collection("messages")
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(10)
                    .get();

            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            System.out.printf("[INFO] Last %d messages\n", documents.size());

            for (QueryDocumentSnapshot document : documents) {
                String content = document.getString("content");
                Timestamp timestamp = document.getTimestamp("timestamp");
                String sender = document.getString("sender");

                System.out.printf("[INFO] Message from %s at %s: %s\n",
                        sender,
                        timestamp != null ? timestamp.toDate() : "pending",
                        content);
            }

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to read messages: " + e.getMessage());
        }
    }

    public void run() {
        System.out.println("Application is running");

        window.setGuiRenderFunction(()-> {
            loginWindow.render();

            if (AppManager.currentUser != null) {
                toolBar();
                communityWindow.render();
                if (AppManager.currentUser.isHRD()) {
                    hrdWindow.render();
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
