package models;

import java.util.ArrayList;
import java.util.List;

public class Event extends ModelBase {
    private final String title;
    private List<Mahasiswa> mhsList = new ArrayList<>();

    public Event(String title) {
        super();
        this.title = title;
    }

    public void printInfo() {
        System.out.println("============");
        System.out.printf("Info Event: %s\n", title);
        System.out.printf("Jumlah Partisipan: %d\n", mhsList.size());
        System.out.println("Mahasiswa yang mengikuti:");
        for (Mahasiswa mhs : mhsList) {
            System.out.println(mhs);
        }
        System.out.println("============");
    }

    public void addMahasiswa(Mahasiswa mhs) {
        mhsList.add(mhs);
    }

    public List<Mahasiswa> getMahasiswa() {
        return mhsList;
    }

    public String getTitle() {
        return title;
    }
}
