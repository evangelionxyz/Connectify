package views;

import core.AppManager;
import imgui.ImGui;
import imgui.ImVec2;
import models.Event;
import models.Mahasiswa;

public class EventWindow extends WindowBase {

    @Override
    public void init() {

    }

    @Override
    public void render() {
        ImGui.begin("Event");
        ImGui.beginChild("##event_list", new ImVec2(210, 0.0f), true);
        {
            //Mahasiswa mahasiswa = (Mahasiswa)AppManager.currentUser;
        }
        ImGui.endChild();
        ImGui.end();
    }
}
