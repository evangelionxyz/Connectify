package views;

import com.google.cloud.Timestamp;
import core.AppManager;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.*;
import imgui.internal.ImGui;
import imgui.internal.ImGuiWindow;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import models.*;

import java.util.ArrayList;
import java.util.List;

public class HRDWindow extends WindowBase {
    private final ImBoolean imCreateWin = new ImBoolean(false);
    private final ImBoolean imDeleteConfirmWin = new ImBoolean(false);
    private final ImBoolean imCreateQuestWin = new ImBoolean(false);
    private final ImBoolean imSendToComWin = new ImBoolean(false);
    private final ImBoolean imCreateAchWin = new ImBoolean(false);

    private final ImString questTitleInput = new ImString(256);
    private final ImString questDescInput = new ImString(256);

    private final ImString achTitleInput = new ImString(256);
    private final ImString achTagInput = new ImString(256);

    private List<String> achTags = new ArrayList<>();

    private final ImString userChatInput = new ImString(256);
    private final ImString eventTitleInput = new ImString(128);
    private final ImString eventDescInput = new ImString(256);
    private final ImString eventTitle = new ImString(256);
    private final ImString eventDesc = new ImString(256);

    @Override
    public void init() {
    }

    private void createWindow() {
        ImGui.begin("Create new Event", imCreateWin, ImGuiWindowFlags.AlwaysAutoResize);
        ImGui.inputText("Title", eventTitleInput);
        ImGui.inputTextMultiline("Description", eventDescInput);

        Runnable create = () -> {
            if (eventTitleInput.isNotEmpty()) {
                Event newEvent = new Event(eventTitleInput.get(), eventDescInput.get());
                newEvent.setCreatorId(AppManager.currentUser.getId());
                newEvent.setDescription(eventDescInput.get());
                AppManager.storeEventToDatabase(newEvent);
                eventTitleInput.clear();
                eventDescInput.clear();
            }
        };

        if (ImGui.button("Create")) {
            create.run();
            imCreateWin.set(false);
        }
        ImGui.sameLine();
        if (ImGui.button("Cancel")) {
            imCreateWin.set(false);
            eventTitleInput.clear();
            eventDescInput.clear();
        }
        ImGui.end();
    }

    private void deleteConfirmationWindow() {
        ImGui.begin("Delete Confirmation", imDeleteConfirmWin, ImGuiWindowFlags.AlwaysAutoResize);
        ImGui.text("Are you sure to delete this event?");

        if (ImGui.button("Delete")) {
            AppManager.removeDocument("events", AppManager.selectedEvent.getId());
            AppManager.selectedEvent = null;
            imDeleteConfirmWin.set(false);
        }
        ImGui.sameLine();
        if (ImGui.button("Cancel")) {
            imDeleteConfirmWin.set(false);
        }
        ImGui.end();
    }

    private void createQuestWindow() {
        ImGui.begin("Create Quest", imCreateQuestWin, ImGuiWindowFlags.AlwaysAutoResize);
        ImGui.inputText("Title", questTitleInput);
        ImGui.inputTextMultiline("Description", questDescInput);

        if (ImGui.button("Create")) {
            if (AppManager.selectedEvent != null) {
                Quest quest = new Quest(questTitleInput.get(), questDescInput.get());
                AppManager.storeQuestToEvent(quest, AppManager.selectedEvent);
                imCreateQuestWin.set(false);
                questTitleInput.clear();
                questDescInput.clear();
            }
        }

        ImGui.sameLine();
        if (ImGui.button("Cancel")) {
            imCreateQuestWin.set(false);
        }
        ImGui.end();
    }

    private void sendToCommunityWindow() {
        ImGui.begin("Send to community", imSendToComWin);

        final float buttonHeight = 25.0f;
        final float availableHeight = imgui.ImGui.getContentRegionAvailY() - buttonHeight - 10;

        ImGui.beginChild("##top_com", new ImVec2(0.0f, availableHeight), true);
        for (Community cm : AppManager.communities) {
            boolean selected = AppManager.selectedCommunity != null && AppManager.selectedCommunity.getId().equals(cm.getId());
            int treeFlags = (selected ? ImGuiTreeNodeFlags.Selected : ImGuiTreeNodeFlags.None) | ImGuiTreeNodeFlags.Leaf
                    | ImGuiTreeNodeFlags.Framed | ImGuiTreeNodeFlags.SpanFullWidth;

            if (selected) imgui.ImGui.pushStyleColor(ImGuiCol.Header, new ImVec4(0.123f, 0.123f, 0.7633f, 1.0f));
            imgui.ImGui.pushStyleColor(ImGuiCol.HeaderHovered, new ImVec4(0.1f, 0.1f, 0.5f, 1.0f));
            imgui.ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, new ImVec2(10.0f, 10.0f));

            if (imgui.ImGui.treeNodeEx(cm.getId(), treeFlags, cm.getName())) {
                if (imgui.ImGui.isItemClicked(ImGuiMouseButton.Left)) {
                    AppManager.selectedCommunity = cm;
                }
                imgui.ImGui.treePop();
            }

            imgui.ImGui.popStyleVar(1);
            imgui.ImGui.popStyleColor(1);
            if (selected) imgui.ImGui.popStyleColor(1);
        }
        ImGui.endChild();

        ImGui.beginChild("##bottom_com");
        if (ImGui.button("Send")) {
            if (AppManager.selectedEvent != null) {
                EventChat newChat = new EventChat(userChatInput.get(), Timestamp.now(), AppManager.currentUser);
                newChat.setEvent(AppManager.selectedEvent);
                AppManager.storeChatToDatabase(newChat);
                AppManager.storeChatToCommunity(newChat, AppManager.selectedCommunity);
                AppManager.storeEventToCommunity(AppManager.selectedEvent, AppManager.selectedCommunity);
                userChatInput.clear();

                imSendToComWin.set(false);
                AppManager.selectedCommunity = null;
            }
        }

        ImGui.sameLine();
        if (ImGui.button("Cancel")) {
            imSendToComWin.set(false);
            AppManager.selectedCommunity = null;
        }

        ImGui.endChild();
        ImGui.end();
    }

    private void createAchievementWindow() {
        ImGui.begin("Create Achievement", imCreateAchWin, ImGuiWindowFlags.AlwaysAutoResize);

        ImGui.inputText("Title", achTitleInput);
        ImGui.inputText("##New Tag", achTagInput);

        ImGui.sameLine();
        if (ImGui.button("Add Tag")) {
            if (achTagInput.isNotEmpty()) {
                achTags.add(achTagInput.get());
                achTagInput.clear();
            }
        }

        for (int i = 0; i < achTags.size(); ++i) {
            if (ImGui.button(achTags.get(i))) {
                achTags.remove(i);
            }

            if (i < achTags.size() - 1){
                ImGui.sameLine();
            }
        }

        if (ImGui.button("Create") && AppManager.selectedQuest != null) {
            Achievement ach = new Achievement(achTitleInput.get());
            achTags.forEach(ach::addTags);
            AppManager.storeAchievementToQuest(ach, AppManager.selectedQuest);

            achTitleInput.clear();
            achTags.clear();
            imCreateAchWin.set(false);
        }

        ImGui.sameLine();
        if (ImGui.button("Cancel")) {
            achTitleInput.clear();
            achTags.clear();
            imCreateAchWin.set(false);
        }
        ImGui.end();
    }

    @Override
    public void render() {

        if (imCreateWin.get()) {
            createWindow();
        } else if (imDeleteConfirmWin.get()) {
            deleteConfirmationWindow();
        } else if (imCreateQuestWin.get()) {
            createQuestWindow();
        } else if (imSendToComWin.get()) {
            sendToCommunityWindow();
        } else if (imCreateAchWin.get()) {
            createAchievementWindow();
        }

        ImGui.begin("Event Manager");

        final float buttonHeight = 25.0f;
        final float availableHeight = imgui.ImGui.getContentRegionAvailY() - buttonHeight - 10;
        final float listWidth = 210.0f;

        // LEFT SECTION
        ImGui.beginChild("##events", new ImVec2(listWidth, 0.0f), true);
        {
            ImGui.beginChild("##event_list", new ImVec2(0.0f, availableHeight), true);
            {
                for (Event ev : AppManager.events) {
                    boolean selected = AppManager.selectedEvent != null && AppManager.selectedEvent.getId().equals(ev.getId());

                    int treeFlags = (selected ? ImGuiTreeNodeFlags.Selected : ImGuiTreeNodeFlags.None) | ImGuiTreeNodeFlags.Leaf
                            | ImGuiTreeNodeFlags.Framed | ImGuiTreeNodeFlags.SpanFullWidth;

                    if (selected)
                        imgui.ImGui.pushStyleColor(ImGuiCol.Header, new ImVec4(0.123f, 0.123f, 0.7633f, 1.0f));
                    imgui.ImGui.pushStyleColor(ImGuiCol.HeaderHovered, new ImVec4(0.1f, 0.1f, 0.5f, 1.0f));
                    imgui.ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, new ImVec2(10.0f, 10.0f));

                    boolean opened = imgui.ImGui.treeNodeEx(ev.getId(), treeFlags, ev.getTitle());

                    if (ImGui.isItemClicked(ImGuiMouseButton.Left)) {
                        AppManager.selectedEvent = ev;
                        eventTitle.set(ev.getTitle());
                        eventDesc.set(ev.getDescription());
                    }

                    if (opened) {
                        ImGui.treePop();
                    }

                    ImGui.popStyleVar(1);
                    ImGui.popStyleColor(1);
                    if (selected) {
                        ImGui.popStyleColor(1);
                    }
                }
            }
            ImGui.endChild(); // !event_list


            ImGui.beginChild("##event_list_action_bar", new ImVec2(0.0f, 0.0f), true);
            {
                if (imgui.ImGui.button("Create Event")) {
                    imCreateWin.set(true);
                }
            }
            ImGui.endChild(); // !event_list_action_bar
        }

        ImGui.endChild(); // !events

        ImGui.sameLine();
        // RIGHT SECTION
        ImGui.beginChild("##event_content", new ImVec2(0.0f, 0.0f), true);
        {
            if (AppManager.selectedEvent != null) {
                ImGui.text(AppManager.selectedEvent.getTitle());
                ImGui.separator();
                if (ImGui.inputText("Title", eventTitle)) {
                    AppManager.selectedEvent.setTitle(eventTitle.get());
                }
                if (ImGui.inputTextMultiline("Description", eventDesc)) {
                    AppManager.selectedEvent.setDescription(eventDesc.get());
                }

                Runnable editEvent = () -> {
                    if (eventTitle.isNotEmpty() && eventDesc.isNotEmpty()) {
                        AppManager.updateEvent(AppManager.selectedEvent);
                    }
                };

                if (ImGui.button("Save", new ImVec2(150.0f, buttonHeight))) {
                    editEvent.run();
                }

                ImGui.sameLine();
                if (ImGui.button("Delete", new ImVec2(150.0f, buttonHeight))) {
                    imDeleteConfirmWin.set(true);
                }

                ImGui.sameLine();
                if (ImGui.button("Create Quest", new ImVec2(150.0f, buttonHeight))) {
                    imCreateQuestWin.set(true);
                }

                ImGui.inputText("Message", userChatInput);
                ImGui.sameLine();
                if (ImGui.button("Send to community", new ImVec2(200.0f, buttonHeight))) {
                    imSendToComWin.set(true);
                }

                // bottom
                ImGui.beginChild("##event_bottom_section");
                {
                    ImGui.beginChild("##event_bottom_left", new ImVec2(listWidth, 0.0f), true);
                    for (Quest quest : AppManager.selectedEvent.getQuests()) {
                        boolean selected = AppManager.selectedEvent != null && AppManager.selectedEvent.getId().equals(quest.getId());
                        int treeFlags = (selected ? ImGuiTreeNodeFlags.Selected : ImGuiTreeNodeFlags.None) | ImGuiTreeNodeFlags.Leaf
                                | ImGuiTreeNodeFlags.Framed | ImGuiTreeNodeFlags.SpanFullWidth;

                        if (selected) imgui.ImGui.pushStyleColor(ImGuiCol.Header, new ImVec4(0.123f, 0.123f, 0.7633f, 1.0f));
                        imgui.ImGui.pushStyleColor(ImGuiCol.HeaderHovered, new ImVec4(0.1f, 0.1f, 0.5f, 1.0f));
                        imgui.ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, new ImVec2(10.0f, 10.0f));

                        if (imgui.ImGui.treeNodeEx(quest.getId(), treeFlags, quest.getTitle())) {
                            if (imgui.ImGui.isItemClicked(ImGuiMouseButton.Left)) {
                                AppManager.selectedQuest = quest;
                            }
                            imgui.ImGui.treePop();
                        }

                        imgui.ImGui.popStyleVar(1);
                        imgui.ImGui.popStyleColor(1);
                        if (selected) imgui.ImGui.popStyleColor(1);
                    }
                    ImGui.endChild();

                    ImGui.sameLine();
                    ImGui.beginChild("##event_bottom_right", new ImVec2(0.0f, 0.0f), true);
                    if (AppManager.selectedQuest != null) {
                        ImGui.text(AppManager.selectedQuest.getTitle());
                        ImGui.text(AppManager.selectedQuest.getDescription());

                        ImGui.pushStyleColor(ImGuiCol.Button, new ImVec4(0.8f, 0.1f, 0.1f, 1.0f));
                        for (int i = 0; i < AppManager.selectedQuest.getAchievements().size(); ++i) {
                            Achievement ach = AppManager.selectedQuest.getAchievements().get(i);
                            ImGui.button(ach.getName());
                        }
                        ImGui.popStyleColor();

                        if (ImGui.button("Create Achievement")) {
                            imCreateAchWin.set(true);
                        }
                    }
                    ImGui.endChild();
                }
                ImGui.endChild();
            }
        }
        ImGui.endChild(); // !event_content

        ImGui.end(); // !event manager
    }
}
