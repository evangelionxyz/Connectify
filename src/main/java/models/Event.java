package models;

import java.util.ArrayList;
import java.util.List;

public class Event extends ModelBase {
    private String title;
    private List<Mahasiswa> mahasiswa;
    private List<Quest> quests;
    private String description;

    private final Community owner;

    public Event(Community owner, String title) {
        super();
        this.title = title;
        this.owner = owner;
        this.mahasiswa = new ArrayList<>();
        this.quests = new ArrayList<>();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addMahasiswa(Mahasiswa mhs) {
        mahasiswa.add(mhs);
    }

    public void addQuest(Quest quest) {
        quests.add(quest);
    }
    
    public List<Quest> getQuests() {
        return quests;
    }

    public Mahasiswa findMahasiswa(String username) {
        for (Mahasiswa mhs : mahasiswa) {
            if (mhs.username.equals(username)) {
                return mhs;
            }
        }
        return null;
    }

    public final List<Mahasiswa> getMahasiswa() {
        return mahasiswa;
    }

    public final String getTitle() {
        return title;
    }
}
