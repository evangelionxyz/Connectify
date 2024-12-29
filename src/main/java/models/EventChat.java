package models;

import com.google.cloud.Timestamp;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class EventChat extends Chat {
    private Event event;
    private String imageId;

    public EventChat() {
        super();
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

    @Override @NotNull
    public Map<String, Object> getStringObjectMap() {
        Map<String, Object> stringObj = new HashMap<>();
        stringObj.put("id", id);
        stringObj.put("message", message);
        stringObj.put("timestamp", timestamp);
        stringObj.put("sender", sender.getId());
        stringObj.put("type", type);
        stringObj.put("eventId", event != null ? event.getId() : "");
        return stringObj;
    }

}
