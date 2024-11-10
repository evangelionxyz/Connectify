package models;

import java.util.ArrayList;
import java.util.List;

public class Mahasiswa extends User{
    private List<Quest> questList;

    private String university;

    Mahasiswa(String name, String university) {
        super(name);
        this.university = university;
        questList = new ArrayList<>();
    }

    public void addQuest(Quest q) {
        questList.add(q);
    }

    public String getUniversity() {
        return university;
    }


}
