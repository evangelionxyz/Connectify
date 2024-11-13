package models;

public class Chat {
    private String message;

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
