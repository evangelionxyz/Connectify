package models;

import java.util.ArrayList;
import java.util.List;

public class HRD extends User {
    private List<Event> events;

    public HRD(String name, String username, String company) {
        super(name, username,"HRD", company);
        this.events = new ArrayList<>();
    }

    public Event addEvent(Community owner, String eventName) {
        Event newEvent = new Event(owner, eventName);
        events.add(newEvent);
        System.out.println("Event " + eventName + " berhasil berhasil dibuat " + getName());
        return newEvent;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void listEvent() {
        if (events.isEmpty()) {
            System.out.println("tidak ada event");
        } else {
            System.out.println("Daftar event yang dibuat oleh: " + getName());
            for (int i = 0; i < events.size(); i++) {
                System.out.println((i + 1) + ". " + events.get(i).getTitle());
            }
        }
    }
}
