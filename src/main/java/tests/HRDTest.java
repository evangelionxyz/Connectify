package tests;

import models.*;

public class HRDTest {
    public static void main(String[] args) {
        User user1 = new User("pratono","pyr", "Owner", "PT Teknologi Maju");
        Community community = new Community("PT Teknologi maju",user1, "1020103");

        HRD hrd = new HRD("Syahdan", "Hexx", "PT Teknologi Maju");

        Event event1 = hrd.addEvent(community, "Hackathon 2024");

        Mahasiswa mhs1 = new Mahasiswa("yuda","yhp","Telkom");
        Mahasiswa mhs2 =new Mahasiswa("andi","and","binus");

        event1.addMahasiswa(mhs1);
        event1.addMahasiswa(mhs2);

        System.out.println("daftar mahasiswa di event: " + event1.getTitle());
        for (Mahasiswa mhs : event1.getMahasiswa()) {
            System.out.println("- " + mhs.getName());
        }
    }
}
