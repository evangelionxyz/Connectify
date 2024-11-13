package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Community extends ModelBase {
    private String name;
    private List<Mahasiswa> listMahasiswa;
    private List<Quest> listQuest;

    public Community(String name) {
        super();
        this.name = name;
        this.listMahasiswa = new ArrayList<>();
        this.listQuest = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Quest> getListQuest() {
        return listQuest;
    }

    public void setListQuest(List<Quest> listQuest) {
        this.listQuest = listQuest;
    }

    public void addMahasiswa(Mahasiswa mhs) {
        if (!listMahasiswa.contains(mhs)) {
            listMahasiswa.add(mhs);
        }
    }

    public boolean isMahasiswaExists(String id){
        for (Mahasiswa mhs : listMahasiswa) {
            if (Objects.equals(mhs.getId(), id)) {
                return true;
            }
        }
        return false;
    }

    public void addQuest(Quest quest) {
        listQuest.add(quest);
    }

    public void addQuestToMahasiswa(Mahasiswa mhs, Quest quest) {
        if (isMahasiswaExists(mhs.getId())) {
            mhs.addQuest(quest);
        }
        System.out.println("Mahasiswa dengan NIM:" + mhs.getId() + "tidak ditemukan");
    }

    public List<Mahasiswa> getMahasiswa() {
        return listMahasiswa;
    }

    public void Printinfo() {
        System.out.println("Nama Mahasiswa: " + name);
        System.out.println("Quest yang tersedia: ");
    }

}
