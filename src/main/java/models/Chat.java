package models;

import com.google.cloud.Timestamp;

public class Chat {
    private String message;
    private Timestamp timestamp;

    public Chat() {
    }

    public Chat(String message) {
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
