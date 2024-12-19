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
        System.out.println("----- Daftar Komunitas -----");
        for (Community com : AppManager.communities)
            System.out.printf("%d. %s\n", ++count, com.getName());
        System.out.println("----------------------");
    }

    private void registrationMenu(Scanner scanner) {
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


    private void menuEvent(Scanner scanner){
        System.out.println("----- Menu Event -----");
        printCommunityList();
        System.out.print("Pilih nama komunitas: ");
        String comName = scanner.nextLine();

        // ---------------------
        // set current selected community
        // ---------------------

        AppManager.selectedCommunity = AppManager.getCommunityByName(comName);
        if (AppManager.selectedCommunity == null) {
            System.out.println("Komunitas tidak ditemukan");
            return;
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

                System.out.println("=======================================");
            }
            case 4 -> {
                System.out.println("Keluar dari menu event.");
                return;
            }
            default -> {
                System.out.println("Pilihan tidak valid.");
            }
        }
    }

    private void communityMenu(Scanner scanner) {
        // Menu Community
        System.out.println("=======================================");
        if (AppManager.currentUser == null) {
            System.out.println("Silahkan login terlebih dahulu");
            System.out.println("=======================================");
            return;
        }

        System.out.println("Selamat datang di menu Community");
        System.out.println("Pilih opsi:");

        System.out.println("1. Menu Event");
        System.out.println("2. Buat Komunitas (untuk HRD)");
        System.out.println("3. Daftar Komunitas (untuk Mahasiswa)");
        System.out.println("4. Keluar menu komunitas");
        System.out.print("Masukkan pilihan: ");

        int communityChoice = scanner.nextInt();
        scanner.nextLine(); // add newline

        switch (communityChoice) {
            case 1 -> {
                System.out.println("----- Menu Event -----");
                printCommunityList();
                System.out.print("Pilih nama komunitas: ");
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

                        System.out.println("=======================================");
                    }
                    case 4 -> {
                        System.out.println("Keluar dari menu event...");
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

                        // Cek apakah currentUser adalah instance dari Mahasiswa
                        if (AppManager.currentUser instanceof Mahasiswa) {
                            Mahasiswa mahasiswa = (Mahasiswa) AppManager.currentUser;
                            if (!selectedCommunity.getMahasiswaIds().contains(mahasiswa.getId())) {
                                selectedCommunity.addMahasiswa(mahasiswa);
                                System.out.println("Anda berhasil bergabung ke komunitas " + selectedCommunity.getName());
                            } else {
                                System.out.println("Anda sudah menjadi bagian dari komunitas ini.");
                            }
                        } else {
                            // Jika currentUser bukan Mahasiswa, coba konversi User ke Mahasiswa
                            System.out.println("User saat ini bukan mahasiswa.");
                            System.out.println("currentUser adalah objek dari kelas: " + AppManager.currentUser.getClass().getName());

                            // Konversi User menjadi Mahasiswa
                            if (AppManager.currentUser instanceof User) {
                                User user = AppManager.currentUser;

                                // Buat Mahasiswa baru berdasarkan data User
                                // Di sini, kita menggunakan konstruktor Mahasiswa yang memerlukan name, username, dan company
                                Mahasiswa mahasiswa = new Mahasiswa(user.getName(), user.getUsername(), user.getCompany());

                                // Masukkan mahasiswa ke komunitas
                                if (!selectedCommunity.getMahasiswaIds().contains(mahasiswa.getId())) {
                                    selectedCommunity.addMahasiswa(mahasiswa);
                                    System.out.println("Anda berhasil bergabung ke komunitas " + selectedCommunity.getName());
                                } else {
                                    System.out.println("Anda sudah menjadi bagian dari komunitas ini.");
                                }

                                // Update currentUser menjadi objek Mahasiswa yang baru
                                AppManager.currentUser = mahasiswa;
                            }
                        }
                    } else {
                        System.out.println("Komunitas tidak ditemukan.");
                    }

                    /*
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
                    */
                }
            }
            case 4 -> {
                System.out.println("Keluar dari menu komunitas...");
            }
        }
    }

    private void loginMenu(Scanner scanner) {
        System.out.println("=======================================");
        System.out.println("Selamat datang kembali silahkan login");
        System.out.print("Masukkan username: ");
        String nama = scanner.nextLine();
        System.out.print("Masukan password: ");
        String password = scanner.nextLine();
        AppManager.currentUser = AppManager.loginUser(nama, password);
        if (AppManager.currentUser != null) {
            System.out.println("-> Selamat datang " + nama);
        } else {
            System.out.println("Username tidak ada, silahkan registrasi terlebih dahulu");
        }
        System.out.println("=======================================");
    }

    private void dataUserMenu() {
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

    private void dataCommunityMenu() {
        int i = 1;
        System.out.println("=======================================");
        System.out.println("-------- Daftar Komunitas --------");
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
                    System.out.printf("%d. %s: %s\n", i, event.getTitle(), event.getDescription());
                    i++;
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

    public void run() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            int option = menu(scanner);
            switch (option) {
                case 1 -> registrationMenu(scanner);
                case 2 -> loginMenu(scanner);
                case 3 -> communityMenu(scanner);
                case 4 -> dataUserMenu();
                case 5 -> dataCommunityMenu();
                case 6 -> running = false;
                default -> System.out.println("Pilihan tidak valid");
            }
        }
    }

    private int menu(Scanner scanner) {
        while (true) {
            System.out.println("------ Connectify ------");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Menu Community");
            System.out.println("4. Tampilkan Data User");
            System.out.println("5. Tampilkan Data Community");
            System.out.println("6. Keluar");
            System.out.print("Masukkan pilihan: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            if (choice >= 1 && choice <= 6) {
                return choice;
            } else {
                System.out.println("Mohon pilih 1-9");
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


