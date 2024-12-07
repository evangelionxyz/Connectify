package tests;

import models.ModelBase;
import java.util.ArrayList;
import java.util.List;

public class EventTest extends ModelBase {
    public static void main(String[] args) {

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
