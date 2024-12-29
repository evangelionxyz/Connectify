package views;

import core.AppManager;
import imgui.ImGui;
import imgui.ImVec2;
import models.Event;
import models.Mahasiswa;
import models.Quest;

import java.util.Map;

public class QuestWindow extends WindowBase {

    private Mahasiswa currentMahasiswa = null;
    @Override
    public void init() {

    }

    @Override
    public void render() {
        ImGui.begin("Quest");

        final float bottomHeight = 25.0f;
        float availableHeight = ImGui.getContentRegionAvailY() - bottomHeight - 10;

        ImGui.beginChild("##quest_list");
        {
            if (currentMahasiswa == null) {
                currentMahasiswa = (Mahasiswa) AppManager.getUserById(AppManager.currentUser.getId());
            }
            else if (currentMahasiswa != null) {
                for (Map.Entry<Quest, Boolean> entry : currentMahasiswa.getQuests().entrySet()) {
                    ImGui.text(entry.getKey().getTitle());
                }
            }
        }
        ImGui.endChild(); //!quest_list
        ImGui.end(); // !Quest
    }
}
