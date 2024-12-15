package tests;
import java.util.Scanner;
import models.Quest;

public class QuestTest {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Quest quest1 = new Quest("Misi Pertama", "Cari Cewe Cindo");

        System.out.println("Quest: " + quest1.getTitle());
        System.out.println("Deskripsi: " + quest1.getDescription());
        System.out.print("Ingin mengerjakan quest ini? (iya/tidak): ");

        String input = scanner.next();

        if (input.equals("iya")) {
            System.out.println("Sedang mengerjakan quest...");
            System.out.print("Sudah selesai dikerjakan? (iya/tidak): ");
        } else {
            System.out.println("Quest Belum Dikerjakan");
        }

        scanner.close();
    }
}
