package tests;

import models.Community;
import models.Event;
import models.Mahasiswa;
import models.ModelBase;
import java.util.ArrayList;
import java.util.List;

public class EventTest extends ModelBase {
    public static void main(String[] args) {

        Mahasiswa pemilik = new Mahasiswa("evan", "evan", "telu");
        Community community = new Community("Offsider", pemilik);

        Event hackathon = new Event(community, "Hackthon 2024");

        Mahasiswa syahdan = new Mahasiswa("syahdan", "syahdan", "telu");
        Mahasiswa yudha = new Mahasiswa("yuda", "yuda", "telu");
        Mahasiswa nopal = new Mahasiswa("nopal", "nopal", "telu");
        hackathon.addMahasiswa(syahdan);
        hackathon.addMahasiswa(yudha);
        hackathon.addMahasiswa(nopal);

        for (Mahasiswa m : hackathon.getMahasiswa()) {
            System.out.println(m);
        }

        String eventOwner = "HRD";
        String eventTitle = "Heckaton";

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
