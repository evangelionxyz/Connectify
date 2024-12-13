package models;

import java.util.ArrayList;
import java.util.List;

public class Mahasiswa extends User {
    private List<Quest> quests;
    private List<Achievement> achievements;

    public Mahasiswa(String name, String username, String company) {
        super(name, username, "MAHASISWA", company);
        quests = new ArrayList<>();
    }

    public void participateEvent(Event e) {
        e.addMahasiswa(this.id);
    }

    public void addQuest(Quest q) {
        quests.add(q);
    }

    public void addAchievement(Achievement ach) {
        achievements.add(ach);
    }

    public final List<Achievement> getAchievement() {
        return achievements;
    }

    public final List<Quest> getQuests() {
        return quests;
    }


    @Override
    public String toString() {
        return String.format("Name: %s, ID: %s", name, id);
    }
}
