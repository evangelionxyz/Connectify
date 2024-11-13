package core;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.FirestoreClient;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.ImString;
import models.Community;
import models.Event;
import models.Mahasiswa;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Application {

    public static FirebaseApp firebaseApp;
    public static Firestore firestore;

    private final Window window;

    private List<Mahasiswa> mhsList;
    private List<Event> eventList;
    private List<Community> communityList;
    private Community selectedCommunity = new Community("Community");

    public Application(String title) {

        window = new Window(1080, 720, title);
        System.out.println("Application created");

        mhsList = new ArrayList<>();
        eventList = new ArrayList<>();
        communityList = new ArrayList<>();

        Mahasiswa mhs1 = new Mahasiswa("Evan", "Telkom");
        Mahasiswa mhs2 = new Mahasiswa("Syahdan", "Telkom");
        Mahasiswa mhs3 = new Mahasiswa("Yudha", "Telkom");
        
        Mahasiswa mhs4 = new Mahasiswa("Gama", "Telkom");
        Mahasiswa mhs5 = new Mahasiswa("Gumi", "Telkom");
        Mahasiswa mhs6 = new Mahasiswa("Benaya", "Telkom");

        mhsList.add(mhs1);
        mhsList.add(mhs2);
        mhsList.add(mhs3);
        mhsList.add(mhs4);
        mhsList.add(mhs5);
        mhsList.add(mhs6);

        Community offsiderCommunity = new Community("Offsider");
        Community teluCommunity = new Community("Telu");

        offsiderCommunity.addMahasiswa(mhs1);
        offsiderCommunity.addMahasiswa(mhs2);
        offsiderCommunity.addMahasiswa(mhs3);

        teluCommunity.addMahasiswa(mhs4);
        teluCommunity.addMahasiswa(mhs5);
        teluCommunity.addMahasiswa(mhs6);

        communityList.add(offsiderCommunity);
        communityList.add(teluCommunity);
    }

    public static void initializeFirebase() throws IOException {
        // "D:/Dev/connectify-telu-firebase-adminsdk.json"

        System.out.println("[INFO] Initializing Firebase");
        FileInputStream serviceAccount = new FileInputStream("D:/Dev/connectify-telu-firebase-adminsdk.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials).build();

        firebaseApp = FirebaseApp.initializeApp(options);
        firestore = FirestoreClient.getFirestore(firebaseApp);

        System.out.printf("[INFO] Firebase Name: %s\n", firebaseApp.getName());
        System.out.println("[INFO] Firebase initialized");
        System.out.println("[INFO] Firestore initialized");
    }

    private void userInfo() {
        ImGui.begin("##user_info");
        ImGui.text("Evangelion");
        ImGui.end();
    }

    public static void sendMessageToFirestore(String message) {
        try {
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("content", message);
            messageData.put("timestamp", FieldValue.serverTimestamp());
            messageData.put("sender", "TestUser");

            firestore.collection("messages").document()
                    .set(messageData)
                    .get();

            System.out.printf("[INFO] Message sent: %s\n", message);

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to send message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void readMessageFromFirestore() {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection("messages")
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
            e.printStackTrace();
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
            for (Mahasiswa mhs : selectedCommunity.getMahasiswa()) {
                ImGui.text(mhs.toString());
            }
            ImGui.endChild();
        }

        {
            ImGui.beginChild("##community_send_message", new ImVec2(0.0f, sendChatHeight), true);
            ImString imString = new ImString();

            Runnable sendMessage = () -> {
              if (imString.get() != null && !imString.get().trim().isEmpty()) {
                  sendMessageToFirestore(imString.get());
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
            userInfo();
            communityPage();
            ImGui.showDemoWindow();
        });

        while (window.isLooping()) {
            window.pollEvents();
            window.swapBuffer();
        }
        window.destroy();
    }
}
