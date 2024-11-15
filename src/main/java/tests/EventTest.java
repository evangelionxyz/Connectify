package tests;

import models.Event;
import models.Mahasiswa;

public class EventTest {
    public static void main(String[] args) {
        Event event = new Event("Event 1");
        Event event2 = new Event("Event 2");
        Event event3 = new Event("Event 2");

        event.printInfo();
    }
}
