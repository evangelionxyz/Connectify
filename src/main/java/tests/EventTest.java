package tests;

import models.Community;
import models.Event;
import models.Mahasiswa;
import models.Quest;
import java.util.ArrayList;
import java.util.List;

public class EventTest {
    public static void main(String[] args) {
        Mahasiswa pemilik = new Mahasiswa("evan", "evan", "telu");
        Community community = new Community("Offsider", pemilik.getId());

        Event hackathon = new Event("Hackathon 2024", "GDSC");

        Mahasiswa syahdan = new Mahasiswa("syahdan", "syahdan", "telu");
        Mahasiswa yudha = new Mahasiswa("yuda", "yuda", "telu");
        Mahasiswa nopal = new Mahasiswa("nopal", "nopal", "telu");
        hackathon.addMahasiswaId(syahdan.getId());
        hackathon.addMahasiswaId(yudha.getId());
        hackathon.addMahasiswaId(nopal.getId());

        Quest quest1 = new Quest("Buat Presentasi", "Membuat Presentasi tentang C++");
        Quest quest2 = new Quest("Bentuk Kelompok", "Buat Kelompok Maksimal 5 orang dan berfoto bersama mereka");
        Quest quest3 = new Quest("Membuat Program", "Buatlah Program");
        Quest quest4 = new Quest("Buatlah Desain Sederhana", "Buat Desain sederhana yang memiliki arti");
        hackathon.addQuest(quest1);
        hackathon.addQuest(quest2);
        hackathon.addQuest(quest3);
        hackathon.addQuest(quest4);

        System.out.println("Mahasiswa yang terdaftar pada event:");
        for (String m : hackathon.getMahasiswaIds()) {
            System.out.println("- " + m);
        }

        String eventOwner = "HRD";
        String eventTitle = "Hackthon 2024";

        List<String> students = new ArrayList<>();
        students.add("Evangelion");
        students.add("Yudha Harwanto");
        students.add("Yuli");
        students.add("Asep Bengkel");

        System.out.println("Nama Event: " + eventTitle);
        System.out.println("Penyelenggara: " + eventOwner);

        System.out.println("Mahasiswa yang terdaftar pada event:");
        for (String student : students) {
            System.out.println("- " + student);
        }

        String foundStudent = findStudent(students, "Evangelion");
        if (foundStudent != null) {
            System.out.println("Mahasiswa ditemukan: " + foundStudent);
        } else {
            System.out.println("Mahasiswa tidak ditemukan.");
        }

        System.out.println("Quest yang Wajib dikerjakan:");
        for (String questId : hackathon.getQuestIDs()) {
            System.out.println(questId);
        }
    }

    private static String findStudent(List<String> students, String username) {
        for (String student : students) {
            if (student.equals(username)) {
                return student;
            }
        }
        return null;
    }
}
