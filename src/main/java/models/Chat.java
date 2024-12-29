package models;

import com.google.cloud.Timestamp;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Chat extends ModelBase {
    protected String message;
    protected Timestamp timestamp;
    protected User sender;
    protected String type;

    public Chat() {
    }

    public Chat(String message, Timestamp timestamp, User sender) {
        super();
        this.message = message;
        this.timestamp = timestamp;
        this.sender = sender;
        this.type = "default";
    }

    public Chat(String message, Timestamp timestamp, User sender, String id) {
        super(id);
        this.message = message;
        this.timestamp = timestamp;
        this.sender = sender;
        this.type = "default";
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

    public final String getType() { return this.type; }

    @NotNull
    public Map<String, Object> getStringObjectMap() {
        Map<String, Object> stringObj = new HashMap<>();
        stringObj.put("id", id);
        stringObj.put("message", message);
        stringObj.put("timestamp", timestamp);
        stringObj.put("sender", sender.getId());
        stringObj.put("type", type);
        return stringObj;
    }
}
