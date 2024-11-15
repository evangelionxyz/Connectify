package models;

import java.util.ArrayList;
import java.util.List;

public class Mahasiswa extends User {
    private List<Quest> questList;

    public Mahasiswa(String name, String username, String company) {
        super(name, "Mahasiswa", username, company);
        questList = new ArrayList<>();
    }

    public void participateEvent(Event e) {
        e.addMahasiswa(this.name)
    }

    public void addQuest(Quest q) {
        questList.add(q);
    }

    @Override
    public String toString() {
        return String.format("Name: %s, ID: %s", name, id);
    }
}
