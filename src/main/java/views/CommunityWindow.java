package views;

import com.google.cloud.Timestamp;
import core.AppManager;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.*;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import models.Chat;
import models.Community;

public class CommunityWindow extends WindowBase {

    private ImBoolean imCreateWin = new ImBoolean(false);
    private ImString nameInput = new ImString(256);
    private ImString userChatInput = new ImString(256);

    private Community communityContextOpen = null;

    private void createWindow() {
        ImGui.begin("Create new Community", imCreateWin, ImGuiWindowFlags.AlwaysAutoResize);
        ImGui.inputText("Name", nameInput);

        Runnable create = () -> {
            if (nameInput.isNotEmpty()) {
                Community com = new Community(nameInput.get(), AppManager.currentUser);
                AppManager.createCommunityToDatabase(com);
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
    public void init() {
        AppManager.selectedCommunity = new Community("Community", null);
    }

    @Override
    public void render() {
        if (imCreateWin.get()) {
            createWindow();
        }

        ImGui.begin("Community");
        ImGui.beginChild("##community", new ImVec2(210.0f, 0.0f), true);

        // Calculate height for the main chat content based on remaining space.
        final float bottomHeight = 25.0f;
        float availableHeight = ImGui.getContentRegionAvailY() - bottomHeight - 10;

        {
            ImGui.beginChild("##community_list", new ImVec2(0.0f, availableHeight), true);
            for (Community cm : AppManager.communities) {
                boolean selected = AppManager.selectedCommunity.getId().equals(cm.getId());
                int treeFlags = (selected ? ImGuiTreeNodeFlags.Selected : ImGuiTreeNodeFlags.None) | ImGuiTreeNodeFlags.Leaf
                        | ImGuiTreeNodeFlags.Framed | ImGuiTreeNodeFlags.SpanFullWidth;

                if (selected) ImGui.pushStyleColor(ImGuiCol.Header, new ImVec4(0.123f, 0.123f, 0.7633f, 1.0f));
                ImGui.pushStyleColor(ImGuiCol.HeaderHovered, new ImVec4(0.1f, 0.1f, 0.5f, 1.0f));
                ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, new ImVec2(10.0f, 10.0f));
                // id, flags, label
                boolean opened = ImGui.treeNodeEx(cm.getId(), treeFlags, cm.getName());
                if (ImGui.isItemClicked(ImGuiMouseButton.Left)) {
                    AppManager.selectedCommunity = cm;
                }

                if (ImGui.isItemHovered() && ImGui.isItemClicked(ImGuiMouseButton.Right)) {
                    if (cm.getOwner().getId().equals(AppManager.currentUser.getId())) {
                        ImGui.openPopup("##community_context_menu", ImGuiPopupFlags.AnyPopupLevel);
                        communityContextOpen = cm;
                    }
                }

                if (communityContextOpen != null) {
                    if (ImGui.beginPopup("##community_context_menu")) {
                        if (ImGui.menuItem("Delete")) {
                            AppManager.removeCommunityFromDatabase(communityContextOpen);
                        }
                        ImGui.endPopup();
                    }
                }

                if (opened) {
                    ImGui.treePop();
                }
                ImGui.popStyleVar(1);
                ImGui.popStyleColor(1);
                if (selected)
                    ImGui.popStyleColor(1);
            }

            ImGui.endChild(); // community_list
        }

        if (AppManager.currentUser.isMahasiswa()) {
            ImGui.beginChild("##community_list_action_bar", new ImVec2(0.0f, bottomHeight), true);
            if (ImGui.button("Create Community")) {
                imCreateWin.set(true);
            }
            ImGui.endChild(); // community_list_action_bar
        }

        ImGui.endChild(); // !community

        ImGui.sameLine();

        ImGui.beginChild("##comunity_content", new ImVec2(0.0f, 0.0f));

        {
            ImGui.beginChild("##community_chats", new ImVec2(0.0f, availableHeight), true);
            ImGui.text(AppManager.selectedCommunity.getName());
            ImGui.separator();
            for (Chat c : AppManager.selectedCommunity.getChats()) {
                displayChat(c);
            }
            ImGui.endChild();
        }
        {
            ImGui.beginChild("##community_send_message", new ImVec2(0.0f, bottomHeight), true);
            Runnable sendMessage = () -> {
                if (userChatInput.isNotEmpty()) {
                    Chat newChat = new Chat(userChatInput.get(), Timestamp.now(), AppManager.currentUser);
                    AppManager.addChatToCommunity(AppManager.selectedCommunity, newChat);
                    userChatInput.clear();
                }
            };
            if (ImGui.inputText("##chat", userChatInput, ImGuiInputTextFlags.EnterReturnsTrue)) {
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

    private void displayChat(Chat c) {
        ImGui.text(c.getSender().getUsername());
        ImGui.text(c.getMessage());
        ImGui.separator();
    }
}
