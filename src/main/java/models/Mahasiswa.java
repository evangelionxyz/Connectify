package models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Mahasiswa extends User {
    private final Map<Quest, Boolean> quests;
    private List<Achievement> achievements;

    public Mahasiswa(String name, String username, String company) {
        super(name, username, "MAHASISWA", company);
        quests = new HashMap<>();
    }

    public void addQuest(Quest q) {
        quests.put(q, false);
    }

    public void doQuest(Quest quest) {

    }

    public List<Achievement> getAchievement() {
        return achievements;
    }

    public Map<Quest, Boolean> getQuests() {
        return quests;
    }

    @Override
    public String toString() {
        return String.format("Name: %s, ID: %s", name, id);
    }
}
