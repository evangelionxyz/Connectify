package models;

import java.util.ArrayList;
import java.util.List;

public class Mahasiswa extends User {
    private final List<Quest> questList;
    private String university;

    public Mahasiswa(String name, String university) {
        super(name);

        this.university = university;
        questList = new ArrayList<>();
    }

    public void participateEvent(Event e) {
        e.addMahasiswa(this);
    }

    public void addQuest(Quest q) {
        questList.add(q);
    }

    public String getUniversity() {
        return university;
    }

    @Override
    public String toString() {
        return String.format("Name: %s, ID: %s", name, id);
    }
}
