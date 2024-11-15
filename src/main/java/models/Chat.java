package models;

import com.google.cloud.Timestamp;

public class Chat {
    private String message;
    private Timestamp timestamp;
    private String senderId;

    public Chat() {
    }

    public Chat(String message, String senderId) {
        this.message = message;
        this.senderId = senderId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getSenderId() {
        return senderId;
    }
}
