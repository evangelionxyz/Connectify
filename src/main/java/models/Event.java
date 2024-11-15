package models;

import java.util.List;

public class Event extends ModelBase {
    private String title;
    private List<Mahasiswa> mahasiswa;
    private List<Quest> quests;

    private final Community owner;

    Event(Community owner, String title) {
        super();
        this.title = title;
        this.owner = owner;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addMahasiswa(Mahasiswa mhs) {
        mahasiswa.add(mhs);
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
