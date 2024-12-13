package tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import core.AppManager;
import core.EncryptionUtils;
import kotlin.Unit;
import models.*;

public class UnitTest {

    public UnitTest() {
        System.out.println("Application created");
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        Quest newQuestdummy = new Quest("questTitledummy", "questDescriptiondummy");
        AppManager.quests.add(newQuestdummy);

        // Dummy User
//        User dummyUser = new User("Dummy Name", "dummyUsername", "HRD", "Dummy Company", "password123");
//
//        // Dummy Mahasiswa
//        Mahasiswa dummyMahasiswa = new Mahasiswa("Dummy Mahasiswa", "dummyMahasiswaUsername", "Dummy University");
//        dummyMahasiswa.addQuest(new Quest("Quest 1", "Description for Quest 1"));
//
//        // Dummy HRD
//        HRD dummyHRD = new HRD("Dummy HRD", "dummyHRDUsername", "Dummy Company");
//
//        // Dummy Quest
//        Quest dummyQuest = new Quest("Dummy Quest", "This is a dummy quest description.");
//
//        // Dummy Achievement
//        List<String> tags = new ArrayList<>();
//        tags.add("Tag1");
//        tags.add("Tag2");
//        Achievement dummyAchievement = new Achievement("Dummy Achievement", tags);
//
//        // Dummy Event
//        Event dummyEvent = new Event("Dummy Event");
//        dummyEvent.setDescription("This is a dummy event description.");
//        dummyEvent.addQuest(dummyQuest);
//        dummyEvent.addMahasiswa(dummyMahasiswa);
//
//        // Dummy Community
//        Community dummyCommunity = new Community("Dummy Community", dummyUser);
//        dummyCommunity.addMahasiswa(dummyMahasiswa);
//        dummyCommunity.addQuest(dummyQuest);
//
//        // Dummy Chat
//        Chat dummyChat = new Chat("Hello, this is a dummy chat!", null, dummyUser);
//
//        // Dummy EventChat
//        EventChat dummyEventChat = new EventChat("Event Chat Message", null, dummyUser);
//
//        Quest[] newQuest1 = {new Quest("DPBO tests", "Quiz sub clo dpbo")};
//
//        dummyEventChat.setEvent(dummyEvent);

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
                    } else {
                        System.out.println("Silahkan registrasi ulang");
                    }
                }
                case 2 -> {
                    System.out.println("Selamat datang kembali silahkan login");
                    System.out.print("Masukkan nama: ");
                    String nama = scanner.nextLine();

                    System.out.print("Masukan Password Baru: ");
                    String password = scanner.nextLine();
                    AppManager.currentUser = AppManager.loginUserTest(nama, password);

                    if (AppManager.currentUser != null) {
                        System.out.println("Selamat datang " + nama);
                    } else {
                        System.out.println("Username tidak ada, silahkan registrasi terlebih dahulu");
                    }
                }
                case 3 -> {
                    if (AppManager.currentUser == null) {
                        System.out.println("Silahkan login dahulu!");
                        break;
                    }

                    System.out.println("Selamat datang di menu tambah quest");

                    if (AppManager.currentUser.isHRD()) {
                        System.out.print("Masukan judul Quest: ");
                        String questTitle = scanner.nextLine();

                        System.out.print("Masukan deskripsi quest: ");
                        String questDescription = scanner.nextLine();

                        Quest newQuest = new Quest(questTitle, questDescription);
                        AppManager.quests.add(newQuest);
                        System.out.println("Quest berhasil ditambahkan!");
                    } else {
                        System.out.println("Hanya HRD yang bisa menambahkan quest!");
                    }
                }

                case 4 -> {
                    System.out.println("ini adalah case 4");
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
                    System.out.println("=======================================");
                    System.out.println("Menampilkan komunitas yang tersedia:");

                    if (AppManager.communities.isEmpty()) {
                        System.out.println("Tidak ada komunitas yang tersedia.");
                    } else {
                        for (Community community : AppManager.communities) {
                            System.out.println("Komunitas: " + community.getName());
                            System.out.println("Pemilik: " + (community.getOwner() != null ? community.getOwner().getName() : "Tidak diketahui"));

                            System.out.println("Mahasiswa dalam komunitas:");
                            for (Mahasiswa mahasiswa : community.getMahasiswa()) {
                                System.out.println("- " + mahasiswa.getName());
                            }

                            System.out.println("Daftar quest dalam komunitas:");
                            for (Quest quest : community.getQuests()) {
                                System.out.println("- " + quest.getTitle() + ": " + quest.getDescription());
                            }

                            System.out.println("Percakapan di komunitas:");
                            for (Chat chat : community.getChats()) {
                                System.out.println("[" + (chat.getTimestamp() != null ? chat.getTimestamp().toDate() : "Waktu tidak diketahui") + "] "
                                        + chat.getSender().getName() + ": " + chat.getMessage());
                            }
                            System.out.println("=======================================");
                        }
                    }
                }
                case 7 -> {
                    System.out.println("Daftar Quest Harian:");
                    if (AppManager.quests.isEmpty()) {
                        System.out.println("Belum ada quest yang ditambahkan.");
                    } else {
                        for (Quest quest : AppManager.quests) {
                            System.out.println("- " + quest.getTitle() + ": " + quest.getDescription() +
                                    " | Status: " + (quest.isCompleted() ? "Selesai" : "Belum Selesai"));
                        }
                    }
                }
                case 8 -> {
                    System.out.println("Menampilkan daftar event yang tersedia:");

                    if (AppManager.events.isEmpty()) {
                        System.out.println("Tidak ada event yang tersedia.");
                    } else {
                        for (Event event : AppManager.events) {
                            System.out.println("=======================================");
                            System.out.println("Nama Event: " + event.getTitle());
                            System.out.println("Deskripsi: " + event.getDescription());
                            System.out.println("Penyelenggara (Creator ID): " + event.getCreatorId());

                            System.out.println("Daftar Mahasiswa Terdaftar:");
                            for (String mahasiswaName : event.getMahasiswaIDs()) {
                                System.out.println("- " + mahasiswaName);
                            }

                            System.out.println("Daftar Quest dalam Event:");
                            for (String questId : event.getQuestIDs()) {
                                Quest quest = AppManager.getQuestById(questId);
                                if (quest != null) {
                                    System.out.println("- " + quest.getTitle() + ": " + quest.getDescription());
                                } else {
                                    System.out.println("- Quest dengan ID " + questId + " tidak ditemukan.");
                                }
                                System.out.println("=======================================");
                            }
                        }
                    }
                }
                case 9 -> {
                    System.out.println("Terima kasih, silakan datang kembali");
                    scanner.close();
                    return;
                }
                default -> {
                    System.out.println("Masukan pilihan tidak valid");
                }
            }
        }
    }

    public int menu() {
        Scanner scanner = new Scanner(System.in);
        int choice = 1;
        while (true) {
            System.out.println("____Conectify____");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Tambah Quest");
            System.out.println("4. Tambah Event");
            System.out.println("5. Tampilkan User Data");
            System.out.println("6. Tampilkan Community Data");
            System.out.println("7. Tampilkan Quest Data");
            System.out.println("8. Tampilkan Event Data");
            System.out.println("9. Keluar");
            System.out.print("Masukkan pilihan: ");
            choice = scanner.nextInt();

            if (choice >= 1 && choice <= 9) {
                return choice;
            } else {
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
        UnitTest unitTest = new UnitTest();
        unitTest.run();
    }
}

