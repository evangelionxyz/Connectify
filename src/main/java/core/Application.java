package core;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImString;
import models.Chat;
import models.Community;
import views.LoginWindow;

import java.util.*;

public class Application {

    private final Window window;
    private final List<Community> communityList;
    private Community selectedCommunity = new Community("Community");

    private final LoginWindow loginWindow = new LoginWindow();

    public Application(String title) {

        window = new Window(1080, 720, title);
        System.out.println("Application created");
        communityList = new ArrayList<>();

        Community offsiderCommunity = new Community("Offsider");
        Community teluCommunity = new Community("Telu");

        communityList.add(offsiderCommunity);
        communityList.add(teluCommunity);

        loginWindow.init();
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

    private void communityPage() {
        ImGui.begin("Community");

        ImGui.beginChild("##community_list", new ImVec2(300.0f, 0.0f), true);
        ImGui.alignTextToFramePadding();
        for (Community cm : communityList) {

            boolean selected = selectedCommunity.equals(cm);
            int treeFlags = (selected ? ImGuiTreeNodeFlags.Selected : ImGuiTreeNodeFlags.None)
            | ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.Framed | ImGuiTreeNodeFlags.SpanFullWidth;

            if (selected)
                ImGui.pushStyleColor(ImGuiCol.Header, new ImVec4(0.123f, 0.123f, 0.7633f, 1.0f));

            ImGui.pushStyleColor(ImGuiCol.HeaderHovered, new ImVec4(0.1f, 0.1f, 0.5f, 1.0f));

            ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, new ImVec2(10.0f, 10.0f));
            // id, flags, label
            boolean opened = ImGui.treeNodeEx(cm.getId(), treeFlags, cm.getName());

            if (ImGui.isItemClicked(ImGuiMouseButton.Left)) {
                selectedCommunity = cm;
            }

            if (opened) {
                ImGui.treePop();
            }

            ImGui.popStyleVar(1);

            ImGui.popStyleColor(1);
            if (selected)
                ImGui.popStyleColor(1);
        }
        ImGui.endChild();

        ImGui.sameLine();

        ImGui.beginChild("##comunity_content", new ImVec2(0.0f, 0.0f));
        // Calculate height for the main chat content based on remaining space.
        final float sendChatHeight = 25.0f;
        float availableHeight = ImGui.getContentRegionAvailY() - sendChatHeight - 10;

        {
            ImGui.beginChild("##community_chats", new ImVec2(0.0f, availableHeight), true);
            ImGui.text(selectedCommunity.getName());
            ImGui.separator();
            for (Chat c : selectedCommunity.getChats()) {
                ImGui.text(c.getMessage());
            }
            ImGui.endChild();
        }

        {
            ImGui.beginChild("##community_send_message", new ImVec2(0.0f, sendChatHeight), true);
            ImString imString = new ImString();

            Runnable sendMessage = () -> {
              if (imString.get() != null && !imString.get().trim().isEmpty()) {
                  Chat newChat = new Chat(imString.get(), AppManager.currentUser);
                  AppManager.sendMessageToDb(selectedCommunity, newChat);
                  imString.clear();
              }
            };

            if (ImGui.inputText("##chat",  imString, ImGuiInputTextFlags.EnterReturnsTrue)) {
                sendMessage.run();
            }

            ImGui.sameLine();

            if (ImGui.button("Send", new ImVec2(200.0f, ImGui.getContentRegionAvail().y))) {
                sendMessage.run();
            }

            ImGui.endChild();
        }
        ImGui.endChild();

        ImGui.end();
    }

    public void run() {
        System.out.println("Application is running");

        window.setGuiRenderFunction(()-> {
            loginWindow.render();

            if (AppManager.currentUser != null) {
                toolBar();
                communityPage();
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
