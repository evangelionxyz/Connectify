package models;

import com.google.cloud.Timestamp;

public class Chat {
    private String message;
    private Timestamp timestamp;
    private User sender;
    private String imageId;


    public Chat() {
    }

    public Chat(String message, User sender) {
        this.message = message;
        this.sender = sender;
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

    public User getSender() {
        return sender;
    }
}
