package views;

import core.AppManager;
import imgui.ImVec2;
import imgui.ImVec4;
import imgui.flag.*;
import imgui.internal.ImGui;
import imgui.type.ImBoolean;
import imgui.type.ImString;
import models.Event;

public class HRDWindow extends WindowBase {
    private final ImBoolean imCreateWin = new ImBoolean(false);
    private final ImString eventTitle = new ImString(128);
    private final ImString eventDesc = new ImString(256);
    private Event eventContextOpen = null;

    @Override
    public void init() {
    }

    private void createWindow() {
        ImGui.begin("Create new Event", imCreateWin, ImGuiWindowFlags.AlwaysAutoResize);
        ImGui.inputText("Title", eventTitle);
        ImGui.inputTextMultiline("Description", eventDesc);

        Runnable create = () -> {
            if (eventTitle.isNotEmpty()) {
                Event newEvent = new Event(eventTitle.get(), eventContextOpen.getCommunity());
                newEvent.setCreatorId(AppManager.currentUser.getId());
                newEvent.setDescription(eventDesc.get());
                AppManager.storeEventToDatabase(newEvent);
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

        ImGui.begin("Event Manager");

        final float buttonHeight = 25.0f;
        float availableHeight = imgui.ImGui.getContentRegionAvailY() - buttonHeight - 10;

        // LEFT SECTION
        ImGui.beginChild("##events", new ImVec2(210.0f, 0.0f), true);
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

                    if (ImGui.isItemHovered() && ImGui.isItemClicked(ImGuiMouseButton.Right)) {
                        if (ev.getCreatorId().equals(AppManager.currentUser.getId())) {
                            ImGui.openPopup("##event_context_menu", ImGuiPopupFlags.AnyPopupLevel);
                            eventContextOpen = ev;
                        }
                    }

                    if (eventContextOpen != null) {
                        if (ImGui.beginPopup("##event_context_menu")) {
                            if (ImGui.menuItem("Delete")) {
                                AppManager.removeDocument("events", eventContextOpen.getId());
                                eventContextOpen = null;
                                AppManager.selectedEvent = null;
                            }
                            ImGui.endPopup();
                        }
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
        ImGui.beginChild("##event_content", new ImVec2(0.0f, availableHeight), true);
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
            }
        }
        ImGui.endChild(); // !event_content

        ImGui.end(); // !event manager
    }
}
