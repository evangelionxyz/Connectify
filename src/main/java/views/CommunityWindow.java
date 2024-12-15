package views;

import com.google.cloud.Timestamp;
import core.AppManager;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.*;
import imgui.type.ImBoolean;
import imgui.type.ImInt;
import imgui.type.ImString;
import models.*;

import java.util.ArrayList;

public class CommunityWindow extends WindowBase {
    private final ImBoolean imCreateWin = new ImBoolean(false);
    private final ImString nameInput = new ImString(256);
    private final ImString userChatInput = new ImString(256);

    private final ImInt imIntIndex = new ImInt(-1);
    private String selectedEventStr;

    private ArrayList<Integer> selectedIndices = new ArrayList<>();
    private String filter = "";

    private Community communityContextOpen = null;

    @Override
    public void init() {
        selectedEventStr = "";
    }

    private void createWindow() {
        ImGui.begin("Create new community", imCreateWin, ImGuiWindowFlags.AlwaysAutoResize);
        ImGui.inputText("Name", nameInput);
        Runnable create = () -> {
            if (nameInput.isNotEmpty()) {
                Community com = new Community(nameInput.get(), AppManager.currentUser.getId());
                AppManager.storeCommunityToDatabase(com);
            }
        };
        if (ImGui.button("Create")) {
            create.run();
            imCreateWin.set(false);
        }
        ImGui.sameLine();
        if (ImGui.button("Cancel")) {
            imCreateWin.set(false);
        }
        ImGui.end();
    }

    @Override
    public void render() {
        if (imCreateWin.get()) {
            createWindow();
        }

        ImGui.begin("Community");

        // Calculate height for the main chat content based on remaining space.
        final float bottomHeight = 25.0f;
        float availableHeight = ImGui.getContentRegionAvailY() - bottomHeight - 10;
//
        // LEFT SECTION
        ImGui.beginChild("##communities", new ImVec2(210.0f, 0.0f), true);
        {
            ImGui.beginChild("##community_list", new ImVec2(0.0f, availableHeight), true);
            {
                for (Community cm : AppManager.communities) {
                    boolean selected = AppManager.selectedCommunity != null && AppManager.selectedCommunity.getId().equals(cm.getId());
                    int treeFlags = (selected ? ImGuiTreeNodeFlags.Selected : ImGuiTreeNodeFlags.None) | ImGuiTreeNodeFlags.Leaf
                            | ImGuiTreeNodeFlags.Framed | ImGuiTreeNodeFlags.SpanFullWidth;

                    if (selected) ImGui.pushStyleColor(ImGuiCol.Header, new ImVec4(0.123f, 0.123f, 0.7633f, 1.0f));
                    ImGui.pushStyleColor(ImGuiCol.HeaderHovered, new ImVec4(0.1f, 0.1f, 0.5f, 1.0f));
                    ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, new ImVec2(10.0f, 10.0f));

                    if (ImGui.treeNodeEx(cm.getId(), treeFlags, cm.getName())) {
                        if (ImGui.isItemClicked(ImGuiMouseButton.Left)) {
                            AppManager.selectedCommunity = cm;
                        }

                        if (ImGui.isItemHovered() && ImGui.isItemClicked(ImGuiMouseButton.Right)) {
                            if (cm.getOwnerId().equals(AppManager.currentUser.getId())) {
                                ImGui.openPopup("##community_context_menu", ImGuiPopupFlags.AnyPopupLevel);
                                communityContextOpen = cm;
                            }
                        }

                        if (communityContextOpen != null) {
                            if (ImGui.beginPopup("##community_context_menu")) {
                                if (ImGui.menuItem("Delete")) {
                                    AppManager.removeDocument("communities", communityContextOpen.getId());
                                }
                                ImGui.endPopup();
                            }
                        }
                        ImGui.treePop();
                    }

                    ImGui.popStyleVar(1);
                    ImGui.popStyleColor(1);
                    if (selected) ImGui.popStyleColor(1);
                }
            }
            ImGui.endChild(); // !community_list

            if (AppManager.currentUser.isMahasiswa()) {
                ImGui.beginChild("##community_list_action_bar", new ImVec2(0.0f, 0.0f), true);
                if (ImGui.button("Create Community")) {
                    imCreateWin.set(true);
                }
                ImGui.endChild(); // community_list_action_bar
            }
        }
        ImGui.endChild(); // !communities

//
        ImGui.sameLine();

        // RIGHT SECTION
        ImGui.beginChild("##community_content", new ImVec2(0.0f, 0.0f));
        {
            ImGui.beginChild("##community_chats", new ImVec2(0.0f, availableHeight), true);
            {
                if (AppManager.selectedCommunity != null) {
                    ImGui.text(AppManager.selectedCommunity.getName());
                    ImGui.separator();
                    for (Chat c : AppManager.selectedCommunity.getChats()) {
                        displayChat(c);
                    }
                }
            }
            ImGui.endChild(); // !community_chats

            ImGui.beginChild("##community_send_message", new ImVec2(0.0f, 0.0f), true);
            {
                if (AppManager.currentUser.isMahasiswa()) {
                    Runnable sendMessage = () -> {
                        if (userChatInput.isNotEmpty()) {
                            Chat newChat = new Chat(userChatInput.get(), Timestamp.now(), AppManager.currentUser);
                            AppManager.storeChatToCommunity(newChat, AppManager.selectedCommunity);
                            userChatInput.clear();
                        }
                    };
                    ImGui.inputText("##chat", userChatInput);
                    ImGui.sameLine();
                    if (ImGui.button("Send", new ImVec2(200.0f, ImGui.getContentRegionAvail().y))) {
                        sendMessage.run();
                    }
                    if (ImGui.isKeyPressed(ImGuiKey.Enter)) {
                        sendMessage.run();
                    }
                }
                else {

                    Runnable sendEventChat = () -> {
                        EventChat eventChat = new EventChat(userChatInput.get(), Timestamp.now(), AppManager.currentUser);
                        AppManager.storeChatToCommunity(eventChat, AppManager.selectedCommunity);
                        userChatInput.clear();
                    };

                    if (ImGui.beginCombo("Select events", "Selected "+selectedIndices.size() + " items")) {
                        for (int i = 0; i < AppManager.eventTitles.size(); ++i) {
                            String title = AppManager.eventTitles.get(i);

                            if (!filter.isEmpty() && !title.toLowerCase().contains(filter.toLowerCase())) {
                                continue;
                            }

                            boolean isSelected = selectedIndices.contains(i);

                            if (ImGui.selectable(title, isSelected)) {
                                if (isSelected) {
                                    selectedIndices.remove(Integer.valueOf(i));
                                } else {
                                    selectedIndices.add(i);
                                }
                            }
                        }
                        ImGui.endCombo();
                    }

                    if (!selectedIndices.isEmpty()) {
                        ImGui.text("Selected Events:");
                        for (int index : selectedIndices) {
                            ImGui.text("- "+AppManager.eventTitles.get(index));
                        }
                    }

                    ImGui.sameLine();
                    if (ImGui.button("Send", new ImVec2(200.0f, ImGui.getContentRegionAvail().y))) {
                        sendEventChat.run();
                    }
                }


            }
            ImGui.endChild(); // !community_send_message

        }
        ImGui.endChild(); //!community_content

        ImGui.end();
    }

    private void displayChat(Chat c) {
        ImGui.text(c.getSender().getUsername());
        if (c.getType().equals("default")) {
            ImGui.text(c.getMessage());
        } else if (c.getType().equals("event")) {
            EventChat ec = (EventChat) c;
            if (ec.getEvent() != null) {
                ImGui.text("Attachment");
                Event ev = ec.getEvent();

                ImGui.text(ev.getTitle());

                if (AppManager.currentUser instanceof Mahasiswa) {
                    if (ImGui.button("Apply")) {
                        System.out.println("here here bandage");
                    }
                }
            }

        }

        ImGui.text(c.getTimestamp().toString());

        ImGui.separator();
    }
}
