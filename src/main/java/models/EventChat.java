package models;

import com.google.cloud.Timestamp;

public class EventChat extends Chat {
    private Event event;
    private String imageId;

    public EventChat() {
        super.type = "event";
    }

    public EventChat(String message, Timestamp timestamp, User sender) {
        super(message, timestamp, sender);
        super.type = "event";
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return this.event;
    }

}
