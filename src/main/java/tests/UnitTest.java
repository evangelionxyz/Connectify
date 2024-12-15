package tests;

import java.util.*;

import core.AppManager;
import core.EncryptionUtils;
import models.*;

public class UnitTest {

    public UnitTest() {
        System.out.println("Application created");
    }

    private void printEventList() {
        int count = 0;
        System.out.println("------------ Daftar Event ------------");
        for (Event event : AppManager.events) {
            System.out.printf("%d. %s\n", ++count, event.getTitle());
        }
        System.out.println("\n");
    }

    private void printCommunityList() {
        int count = 0;
        System.out.println("------------ Daftar Komunitas ------------");
        for (Community com : AppManager.communities) {
            System.out.printf("%d. %s\n", ++count, com.getName());
        }
        System.out.println("\n");
    }

    private void communityMenu(Scanner scanner) {
        // Menu Community
        System.out.println("=======================================");
        if (AppManager.currentUser == null) {
            System.out.println("Silahkan login terlebih dahulu");
            System.out.println("=======================================");
            return;
        }
        System.out.println("Daftar komunitas yang tersedia:");

        if (AppManager.communities.isEmpty()) {
            System.out.println("Tidak ada komunitas yang tersedia.");
        } else {
            for (Community community : AppManager.communities) {
                System.out.println("- " + community.getName());
            }
        }

        System.out.println("Selamat datang di menu Community");
        System.out.println("Pilih opsi:");
        System.out.println("1. Event Menu");
        System.out.println("2. Buat Komunitas (untuk HRD)");
        System.out.println("3. Daftar Komunitas (untuk Mahasiswa)");
        System.out.print("Masukkan pilihan: ");

        int communityChoice = scanner.nextInt();
        scanner.nextLine();

        switch (communityChoice) {
            case 1 -> {
                System.out.println("Masuk ke menu Event");
                System.out.println("=======================================");

                if (AppManager.currentUser == null) {
                    System.out.println("Silahkan login terlebih dahulu");
                    System.out.println("=======================================");
                    break;
                }

                printCommunityList();

                System.out.print("Pilih komunitas: ");
                String comName = scanner.nextLine();

                // ---------------------
                // set current selected community
                // ---------------------

                AppManager.selectedCommunity = AppManager.getCommunityByName(comName);
                if (AppManager.selectedCommunity == null) {
                    System.out.println("Komunitas tidak ditemukan");
                    break;
                }

                System.out.println("1. Tambah Event (untuk HRD)");
                System.out.println("2. Enroll ke Event (untuk Mahasiswa)");
                System.out.println("3. Kerjakan Quest (untuk Mahasiswa)");
                System.out.println("4. Keluar menu Event");
                System.out.print("Masukkan pilihan: ");

                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> {
                        if (AppManager.currentUser.isHRD()) {

                            System.out.print("Masukkan judul: ");
                            String eventTitle = scanner.nextLine();

                            System.out.print("Masukkan deskripsi: ");
                            String eventDesc = scanner.nextLine();

                            Event newEvent = new Event(eventTitle, eventDesc);
                            newEvent.setCreatorId(AppManager.currentUser.getId());

                            // input quests
                            System.out.println("----- Memasukan Quests -----");

                            while (true) {
                                System.out.print("Masukkan nama quest: ");
                                String questTitle = scanner.nextLine();

                                System.out.print("Masukkan deskripsi quest: ");
                                String questDesc = scanner.nextLine();

                                Quest newQuest = new Quest(questTitle, questDesc);
                                AppManager.storeQuestToEvent(newQuest, newEvent);

                                System.out.print("Lanjut? [y/n]: ");
                                String n = scanner.nextLine();
                                if (n.equalsIgnoreCase("n")) {
                                    break;
                                }
                            }

                            AppManager.storeEventToCommunity(newEvent, AppManager.selectedCommunity);
                            System.out.println("Event berhasil ditambahkan");

                        } else {
                            System.out.println("Hanya HRD yang bisa menambahkan event");
                        }
                        System.out.println("=======================================");
                    }
                    case 2 -> {
                        if (AppManager.currentUser.isMahasiswa()) {

                            printEventList();

                            System.out.print("Masukkan judul event yang ingin diikuti: ");
                            String eventTitle = scanner.nextLine();

                            Event selectedEvent = AppManager.getEventByTitle(eventTitle);
                            if (selectedEvent != null) {

                                // INFO: Currently only working without communities
                                AppManager.storeMahasiswaToEvent((Mahasiswa)AppManager.currentUser, selectedEvent);
                                System.out.printf("Mahasiswa %s berhasil mengikuti event %s\n",
                                        AppManager.currentUser.getName(), selectedEvent.getTitle());

                            } else {
                                System.out.println("Event dengan judul tersebut tidak ditemukan.");
                            }
                        } else {
                            System.out.println("Hanya mahasiswa yang bisa mendaftar ke event.");
                        }
                        System.out.println("=======================================");
                    }
                    case 3 -> {
                        if (!AppManager.currentUser.isMahasiswa()) {
                            System.out.println("Hanya mahasiswa yang bisa mengerjakan quest.");
                            System.out.println("=======================================");
                            break;
                        }

                        int count = 0;
                        boolean hasJoinedEvent = false;

                        System.out.println("Event yang diikuti oleh Anda:");
                        for (Event event : AppManager.events) {
                            if (event.getMahasiswaIds().contains(AppManager.currentUser.getId())) {
                                hasJoinedEvent = true;
                                System.out.printf("%d. %s\n", ++count, event.getTitle());
                                if (!event.getQuests().isEmpty()) {
                                    System.out.println("-> Quests: ");
                                    for (Quest q : event.getQuests()) {
                                        System.out.printf ("    - %s\n", q.getTitle());
                                    }
                                }
                            }
                        }

                        if (!hasJoinedEvent) {
                            System.out.println("Anda belum bergabung dengan event manapun.");
                            System.out.println("=======================================");
                            break;
                        }

                        System.out.print("Masukkan nama event: ");
                        String eventName = scanner.nextLine();
                        System.out.print("Masukkan nama quest: ");
                        String questName = scanner.nextLine();

                        AppManager.selectedEvent = AppManager.getEventByTitle(eventName);
                        Quest selectedQuest = AppManager.getQuestByName(questName);

                        if (AppManager.selectedEvent != null && selectedQuest != null) {

                        }

                        System.out.print("Masukkan nama quest yang ingin dikerjakan: ");
                        String questName = scanner.nextLine();

                        boolean questFound = false;
                        for (Event event : AppManager.events) {
                            if (event.getMahasiswaIds().contains(AppManager.currentUser.getId())) {
                                if (questIndex >= 1 && questIndex <= event.getQuestIDs().size()) {
                                    String questID = event.getQuestIDs().get(questIndex - 1);
                                    Quest selectedQuest = AppManager.getQuestById(questID);
                                    if (selectedQuest != null) {
                                        questFound = true;
                                        if (selectedQuest.isCompleted()) {
                                            System.out.println("Quest '" + selectedQuest.getTitle() + "' sudah selesai dikerjakan.");
                                        } else {
                                            selectedQuest.setCompletion(true);
                                            System.out.println("Quest '" + selectedQuest.getTitle() + "' dari event '" + event.getTitle() + "' berhasil diselesaikan!");

                                            AppManager.updateQuestStatus(AppManager.currentUser, selectedQuest, event);
                                        }
                                    } else {
                                        System.out.println("Quest dengan ID " + questID + " tidak ditemukan.");
                                    }
                                    break;
                                }
                            }
                        }

                        if (!questFound) {
                            System.out.println("Nomor quest tidak valid atau Anda tidak terdaftar dalam event ini.");
                        }

                        System.out.println("=======================================");
                    }
                    default -> {
                        System.out.println("Pilihan tidak valid.");
                    }
                }
            }
            case 2 -> {
                if (AppManager.currentUser.isHRD()) {
                    System.out.print("Masukkan nama komunitas: ");
                    String communityName = scanner.nextLine();
                    Community newCommunity = new Community(communityName, AppManager.currentUser.getId());
                    AppManager.communities.add(newCommunity);
                    System.out.println("Komunitas berhasil dibuat!");
                } else {
                    System.out.println("Hanya HRD yang bisa membuat komunitas.");
                }
            }
            case 3 -> {
                System.out.println("Daftar komunitas yang tersedia:");
                if (AppManager.communities.isEmpty()) {
                    System.out.println("Tidak ada komunitas yang tersedia.");
                } else {
                    for (int i = 0; i < AppManager.communities.size(); i++) {
                        Community community = AppManager.communities.get(i);
                        User owner = AppManager.getUserById(community.getOwnerId());
                        System.out.printf("%d. %s - pemilik: %s\n", i + 1, community.getName(),
                                owner != null ? owner.getName() : " ");
                    }

                    System.out.print("Masukkan nomor komunitas yang ingin Anda ikuti: ");
                    int communityIndex = scanner.nextInt();
                    scanner.nextLine();

                    if (communityIndex >= 1 && communityIndex <= AppManager.communities.size()) {
                        Community selectedCommunity = AppManager.communities.get(communityIndex - 1);
                        if (!selectedCommunity.getMahasiswaIds().contains(AppManager.currentUser.getId())) {
                            selectedCommunity.addMahasiswa((Mahasiswa) AppManager.currentUser);
                            System.out.println("Anda berhasil bergabung ke komunitas " + selectedCommunity.getName());
                        } else {
                            System.out.println("Anda sudah menjadi bagian dari komunitas ini.");
                        }
                    } else {
                        System.out.println("Komunitas tidak ditemukan.");
                    }
                }
            }
        }
    }



    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            int option = menu();

            switch (option) {
                case 1 -> {
                    System.out.println("=======================================");
                    System.out.println("Selamat datang di menu registrasi");
                    System.out.print("Masukkan nama: ");
                    String nama = scanner.nextLine();

                    System.out.print("Masukan Password Baru: ");
                    String password = scanner.nextLine();

                    System.out.print("Masukan role(mhs, HRD)");
                    String type = scanner.nextLine();

                    User user = new User(nama, nama, type, "Telu", password);

                    if (AppManager.registerUser(user)) {
                        System.out.println("Registrasi berhasil! Selamat Datang, " + nama);
                        System.out.println("Anda akan diarahkan keberanda");
                    } else {
                        System.out.println("Silahkan registrasi ulang");
                    }
                    System.out.println("=======================================");
                }

                case 2 -> {
                    System.out.println("=======================================");
                    System.out.println("Selamat datang kembali silahkan login");
                    System.out.print("Masukkan nama: ");
                    String nama = scanner.nextLine();

                    System.out.print("Masukan Password: ");
                    String password = scanner.nextLine();
                    AppManager.currentUser = AppManager.loginUser(nama, password);

                    if (AppManager.currentUser != null) {
                        System.out.println("Selamat datang " + nama);
                    } else {
                        System.out.println("Username tidak ada, silahkan registrasi terlebih dahulu");
                    }
                    System.out.println("=======================================");
                }
                case 3 -> {
                    System.out.println("=======================================");
                    if (AppManager.currentUser == null) {
                        System.out.println("Silahkan login dahulu!");
                        System.out.println("=======================================");
                        break;
                    }

                    // create quest in an event
                    if (AppManager.currentUser.isHRD()) {
                        System.out.println("Selamat datang di menu tambah quest");

                        printEventList();

                        System.out.print("Pilih event: ");
                        String eventTitle = scanner.nextLine();

                        Event selectedEvent = AppManager.getEventByTitle(eventTitle);
                        
                        if (selectedEvent != null) {
                            System.out.print("Masukan judul Quest: ");
                            String questTitle = scanner.nextLine();

                            System.out.print("Masukan deskripsi quest: ");
                            String questDescription = scanner.nextLine();

                            Quest newQuest = new Quest(questTitle, questDescription);
                            AppManager.storeQuestToEvent(newQuest, selectedEvent);
                            System.out.println("Quest berhasil ditambahkan!");

                        }else {
                            System.out.println("Event tidak ditemukan. Silakan buat event terlebih dahulu.");
                        }
                    } else {
                        System.out.println("Hanya HRD yang bisa menambahkan quest!");
                    }
                    System.out.println("=======================================");
                }
                case 4 -> {
                   communityMenu(scanner);
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

                            User owner = AppManager.getUserById(community.getOwnerId());
                            System.out.printf("Pemilik: %s\n", owner != null ? owner.getName() : "Tidak diketahui");

                            System.out.println("Mahasiswa dalam komunitas:");
                            for (Mahasiswa mahasiswa : community.getMahasiswa()) {
                                System.out.println("- " + mahasiswa.getName());
                            }

                            System.out.println("Daftar event dalam komunitas:");
                            for (Event event : community.getEvents()) {
                                System.out.printf("%s: %s\n", event.getTitle(), event.getDescription());

                                // TODO: Print quests
                            }

                            System.out.println("Percakapan di komunitas:");
                            for (Chat chat : community.getChats()) {
                                System.out.println("-----------------------");
                                System.out.printf("[%s] %s: %s\n", chat.getTimestamp().toString(),
                                        chat.getSender().getName(), chat.getMessage());
                                System.out.println("-----------------------");
                            }
                            System.out.println("=======================================");
                        }
                    }
                }
                case 7 -> {
                    System.out.println("=======================================");
                    if (AppManager.currentUser == null) {
                        System.out.println("Silahkan login terlebih dahulu");
                        System.out.println("=======================================");
                    }

                    System.out.println("Daftar Quest:");
                    if (AppManager.quests.isEmpty()) {
                        System.out.println("Belum ada quest yang ditambahkan.");
                    } else {
                        for (int i = 0; i < AppManager.quests.size(); i++) {
                            Quest quest = AppManager.quests.get(i);
                            System.out.println((i + 1) + ". " + quest.getTitle() + ": " + quest.getDescription() +
                                    " | Status: " + (quest.isCompleted() ? "Selesai" : "Belum Selesai"));
                        }

                        System.out.print("Masukkan nomor quest yang ingin dikerjakan: ");
                        int questIndex = scanner.nextInt();
                        scanner.nextLine();

                        if (questIndex >= 1 && questIndex <= AppManager.quests.size()) {
                            Quest selectedQuest = AppManager.quests.get(questIndex - 1);
                            if (selectedQuest.isCompleted()) {
                                System.out.println("Quest ini sudah selesai dikerjakan.");
                            } else {
                                selectedQuest.doQuest();
                                System.out.println("Quest '" + selectedQuest.getTitle() + "' berhasil diselesaikan!");

                                AppManager.quests.remove(selectedQuest);
                                System.out.println("Quest '" + selectedQuest.getTitle() + "' telah dihapus dari daftar.");
                            }
                        } else {
                            System.out.println("Nomor quest tidak valid.");
                        }
                    }
                    System.out.println("=======================================");
                }
                case 8 -> {
                    System.out.println("Menampilkan daftar event yang tersedia:");

                    if (AppManager.events.isEmpty()) {
                        System.out.println("Tidak ada event yang tersedia.");
                    } else {
                        for (Event event : AppManager.events) {
                            System.out.println("============ Pembatas =============");
                            System.out.println("Nama Event: " + event.getTitle());
                            System.out.println("Deskripsi: " + event.getDescription());
                            System.out.println("Penyelenggara (Creator ID): " + event.getCreatorId());

                            if (!event.getMahasiswaIds().isEmpty()) {
                                System.out.println("Daftar Mahasiswa Terdaftar:");
                                for (String mhsID : event.getMahasiswaIds()) {
                                    User user = AppManager.getUserById(mhsID);
                                    if (user != null) {
                                        System.out.println("- " + user.getName());
                                    }
                                }
                            }

                            System.out.println("Daftar Quest dalam Event:");
                            for (String questId : event.getQuestIDs()) {
                                Quest quest = AppManager.getQuestById(questId);
                                if (quest != null) {
                                    System.out.println("- " + quest.getTitle() + ": " + quest.getDescription());
                                } else {
                                    System.out.println("- Quest dengan ID " + questId + " tidak ditemukan.");
                                }
                            }
                        System.out.println("===================================");
                        }
                    }
                }
                case 9 -> {
                    System.out.println("Terima kasih, silakan datang kembali ^^");
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
            System.out.println("------ Conectify ------");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Tambah Quest");
            System.out.println("4. Menu Event");
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

