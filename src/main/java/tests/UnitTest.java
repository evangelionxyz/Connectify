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
                    String nama = scanner.nextLine();
                    System.out.println("Selamat Datang" + nama);
                }
                case 4 -> {
                    System.out.println("User Data");

                }
                case 5 -> {

                }
                case 6 -> {

                }
                case 7 -> {

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
            System.out.println("3.Beranda");
            System.out.println("4.Tampilkan user data");
            System.out.println("5.Tampilkan community data");
            System.out.println("6.Tampilkan Event Data");
            System.out.println("7.Tampilkan Quest Data");
            System.out.println("8.Keluar");
            System.out.print("Masukkan pilihan: ");
            choice = scanner.nextInt();

            if (choice >= 1 && choice <= 8) {
                return choice;
            }
            else {
                System.out.println("Tolong Pilih 1-8");
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

