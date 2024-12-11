package tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import core.AppManager;
import core.EncryptionUtils;
import kotlin.Unit;
import models.*;
//todo : cara menyelesainkan quest yang di dalam event, kalau udh selesai ditandain dan dapet reward
public class UnitTest {

    public UnitTest() {
        System.out.println("Application created");
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Application running");
        
        while (true) {
            int option = menu();

            switch (option) {
                case 1 -> {
                    System.out.println("Selamat datang di menu registrasi");
                    System.out.print("Masukkan nama: ");
                    String nama = scanner.nextLine();

                    System.out.print("Masukan Password Baru: ");
                    String password = scanner.nextLine();


                    System.out.print("Masukan role(mhs, HRD)");
                    String type = scanner.nextLine();

                    User user = new User(nama, nama, type, "Telu", password);

                    if (AppManager.registerUserTest(user)) {
                        System.out.println("Registrasi berhasil ! Selamat Datang, " + nama);
                        System.out.println("Anda akan diarahkan keberanda");
                        break;
                    } else {
                        System.out.println("Silahkan registrasi ulang");
                    }
                    scanner.close();
                }
                case 2 -> {
                    System.out.println("Selamat datang kembali silahkan login");
                    System.out.print("Masukkan nama: ");
                    String nama = scanner.nextLine();

                    System.out.print("Masukan Password Baru: ");
                    String password = scanner.nextLine();
                    AppManager.currentUser = AppManager.loginUserTest(nama, password);

                    if (AppManager.currentUser != null) {
                        System.out.println("selamat datang " + nama);
                    } else {
                        System.out.println("username tidak ada, silahkan registrasi terlebih dahulu");
                    }
                }
                case 3 -> {
                    System.out.println("selamat datang di menu tambah quest");
                    if (AppManager.currentUser == null) {
                        System.out.println("silahkan login dahulu!");
                        break;
                    }

                    if (AppManager.currentUser instanceof HRD hrd) {
                        System.out.println("menambahkan quest baru");
                        scanner.nextLine();
                        System.out.print("masukan judul Quest");
                        String questTitle = scanner.nextLine();

                        System.out.print("masukan deskripsi quest");
                        String questDescription = scanner.nextLine();


                        Quest newQuest = new Quest(questTitle, questDescription);
                        hrd.equals(newQuest);

                        System.out.println("Quest berhasil ditambahkan!");
                    } else {
                        System.out.println("hanya hrd yang bisa menambahkan quest!");
                    }
                }
                case 4 -> {

                }
                case 5 -> {
                    if (AppManager.currentUser == null) {
                        System.out.println("Silakan login terlebih dahulu untuk melihat data pengguna.");
                    } else {
                        System.out.println("============== Informasi User ==============");
                        User currentUser = AppManager.currentUser;
                        System.out.println("Nama     : " + currentUser.getName());
                        System.out.println("Username : " + currentUser.getUsername());
                        System.out.println("Role     : " + currentUser.getType());
                        System.out.println("Password : " + currentUser.getPassword());
                        System.out.println("============================================");
                    }
                }
                case 6 -> {
                    System.out.println("selamat datang di menu tambah quest");
                }
                case 7 -> {
                    if (AppManager.currentUser == null) {
                        System.out.println("Silakan login terlebih dahulu untuk mengakses quest.");
                        break;
                    }

                    if (AppManager.currentUser instanceof Mahasiswa mahasiswa) {
                        // Display quests for the current logged-in "Mahasiswa"
                        List<Quest> quests = mahasiswa.getQuests();
                        if (quests.isEmpty()) {
                            System.out.println("Tidak ada quest untuk saat ini.");
                            break;
                        }

                        System.out.println("Daftar Quest:");
                        for (int i = 0; i < quests.size(); i++) {
                            System.out.println((i + 1) + ". " + quests.get(i).getTitle());
                        }

                        System.out.print("Pilih nomor quest untuk detail (atau 0 untuk kembali): ");
                        int questChoice = scanner.nextInt();

                        if (questChoice > 0 && questChoice <= quests.size()) {
                            Quest selectedQuest = quests.get(questChoice - 1);
                            System.out.println("Detail Quest:");
                            System.out.println("Judul: " + selectedQuest.getTitle());
                            System.out.println("Deskripsi: " + selectedQuest.getDescription());

                            System.out.print("Tandai quest ini sebagai selesai? (iya/tidak): ");
                            String completeChoice = scanner.next();

                            if (completeChoice.equalsIgnoreCase("iya")) {
                                selectedQuest.doQuest();
                                System.out.println("Quest berhasil diselesaikan!");
                            } else {
                                System.out.println("Quest tidak ditandai sebagai selesai.");
                            }
                        } else {
                            System.out.println("Kembali ke menu utama.");
                        }
                    } else {
                        System.out.println("Hanya mahasiswa yang dapat mengakses quest.");
                    }
                    if (AppManager.currentUser == null) {
                        System.out.println("Silakan login terlebih dahulu untuk mengakses quest.");
                        break;
                    }

                    if (AppManager.currentUser instanceof Mahasiswa mahasiswa) {
                        List<Quest> quests = mahasiswa.getQuests();
                        if (quests.isEmpty()) {
                            System.out.println("Tidak ada quest untuk saat ini.");
                            break;
                        }

                        System.out.println("Daftar Quest:");
                        for (int i = 0; i < quests.size(); i++) {
                            System.out.println((i + 1) + ". " + quests.get(i).getTitle());
                        }

                        System.out.print("Pilih nomor quest untuk detail (atau 0 untuk kembali): ");
                        int questChoice = scanner.nextInt();

                        if (questChoice > 0 && questChoice <= quests.size()) {
                            Quest selectedQuest = quests.get(questChoice - 1);
                            System.out.println("Detail Quest:");
                            System.out.println("Judul: " + selectedQuest.getTitle());
                            System.out.println("Deskripsi: " + selectedQuest.getDescription());

                            System.out.print("Tandai quest ini sebagai selesai? (iya/tidak): ");
                            String completeChoice = scanner.next();

                            if (completeChoice.equalsIgnoreCase("iya")) {
                                selectedQuest.doQuest();
                                System.out.println("Quest berhasil diselesaikan!");
                            } else {
                                System.out.println("Quest tidak ditandai sebagai selesai.");
                            }
                        } else {
                            System.out.println("Kembali ke menu utama.");
                        }
                    } else {
                        System.out.println("Hanya mahasiswa yang dapat mengakses quest.");
                    }
                }
                case 8 -> {
                    System.out.println("terimakasih silakan datang kembali");
                    scanner.close();
                }
                default -> {
                    System.out.println("masukan pilihan tidak valild");
                }
            }
        }
    }

    public int menu() {
        Scanner scanner = new Scanner(System.in);
        int choice = 1;
        while (true) {
            System.out.println("____Conectify____");
            System.out.println("1.Register");
            System.out.println("2.Login");
            System.out.println("3.Tambah Quest");
            System.out.println("4.Tambah Event");
            System.out.println("5.Tampilkan user data");
            System.out.println("6.Tampilkan community data");
            System.out.println("7.Tampilkan Event Data");
            System.out.println("8.Tampilkan Quest Data");
            System.out.println("9.Keluar");
            System.out.print("Maukkan pilihan: ");
            choice = scanner.nextInt();

            if (choice >= 1 && choice <= 9) {
                return choice;
            }
            else {
                System.out.println("Tolong Pilih 1-9");
            }

        }
    }

}


class Test {
    public static void main(String[] args) {
        try {
            EncryptionUtils.initialize();
            AppManager.initializeFirebase();
        } catch (Exception e) {
            System.out.printf("Exception: %s\n", e.getMessage());
        }
        new UnitTest().run();
    }
}

