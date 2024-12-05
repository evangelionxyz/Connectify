package models;

import com.google.cloud.Timestamp;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Chat extends ModelBase {
    private String message;
    private Timestamp timestamp;
    private User sender;
    private String imageId;

    public Chat(String message, Timestamp timestamp, User sender) {
        this.message = message;
        this.timestamp = timestamp;
        this.sender = sender;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public final String getMessage() {
        return message;
    }

    public final Timestamp getTimestamp() {
        return timestamp;
    }

    public final User getSender() {
        return sender;
    }

    @NotNull
    public Map<String, Object> getStringObjectMap() {
        Map<String, Object> stringObj = new HashMap<>();
        stringObj.put("id", id);
        stringObj.put("message", message);
        stringObj.put("timestamp", timestamp);
        stringObj.put("sender", sender.getId());
        return stringObj;
    }
}
