package views;

import core.AppManager;
import imgui.ImGui;
import imgui.ImVec2;
import models.Achievement;
import models.Mahasiswa;
import models.Quest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileWindow extends WindowBase {

    private Mahasiswa currentMahasiswa = null;
    @Override
    public void init() {

    }

    @Override
    public void render() {
        ImGui.begin("Profile");

        final float bottomHeight = 25.0f;
        float availableHeight = ImGui.getContentRegionAvailY() - bottomHeight - 10;

        ImGui.beginChild("##quest_list", new ImVec2(300.0f, 0.0f), true);
        {
            if (currentMahasiswa == null) {
                currentMahasiswa = (Mahasiswa) AppManager.getUserById(AppManager.currentUser.getId());
            }
            else if (currentMahasiswa != null) {
                for (Map.Entry<Quest, Boolean> entry : currentMahasiswa.getQuests().entrySet()) {
                    Quest quest = entry.getKey();
                    ImGui.text(quest.getTitle());

                    // entry value should be false
                    if (ImGui.button("Start quest") && !entry.getValue()) {
                        for (Achievement ach : quest.getAchievements()) {
                            currentMahasiswa.doQuest(quest);
                            AppManager.storeAchievementToMahasiswa(ach, currentMahasiswa);
                        }

                        try {
                            Map<String, Boolean> questMap = new HashMap<>();
                            for (Map.Entry<Quest, Boolean> questEntry : currentMahasiswa.getQuests().entrySet()) {
                                questMap.put(entry.getKey().getId(), entry.getValue());
                            }

                            Map<String, Object> mahasiswaUpdateData = new HashMap<>();
                            mahasiswaUpdateData.put("quests", questMap);

                            AppManager.firestore.collection("users")
                                    .document(currentMahasiswa.getId())
                                    .update(mahasiswaUpdateData);

                        } catch (Exception e) {
                            System.err.println("[ERROR] Failed to update user's achievement." + e.getMessage());
                        }
                    }
                    ImGui.separator();
                }
            }
        }
        ImGui.endChild(); //!quest_list

        ImGui.sameLine();
        ImGui.beginChild("##achievement_list", new ImVec2(0.0f, 0.0f), true);
        {
            if (currentMahasiswa != null) {
                for (Achievement ach : currentMahasiswa.getAchievement()) {
                    ImGui.text(ach.getName());
                    for (int i = 0; i < ach.getTags().size(); ++i) {
                        ImGui.button(ach.getTags().get(i));

                        if (i < ach.getTags().size() - 1) {
                            ImGui.sameLine();
                        }
                    }

                }
            }
        }
        ImGui.endChild(); // !achievement_list

        ImGui.end(); // !Profile
    }
}
