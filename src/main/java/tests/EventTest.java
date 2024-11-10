package tests;

import models.Event;
import models.Mahasiswa;

public class EventTest {
    public static void main(String[] args) {
        Event event = new Event("Event 1");
        Event event2 = new Event("Event 2");
        Event event3 = new Event("Event 2");

        Mahasiswa mhs1 = new Mahasiswa("Evangelion", "Telkom");
        Mahasiswa mhs2 = new Mahasiswa("Syahdan", "Telkom");
        Mahasiswa mhs3 = new Mahasiswa("Yudha", "Telkom");

        mhs1.participateEvent(event);
        mhs2.participateEvent(event);
        mhs3.participateEvent(event);

        event.printInfo();
    }
}
