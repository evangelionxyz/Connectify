package models;

public class Quest extends ModelBase {
    private String title;
    private String description;
    private boolean isCompleted;

    public Quest(String title, String description) {
        this.title = title;
        this.description = description;
        this.isCompleted = false;
    }


    public void doQuest() {
        if (isCompleted) {
            System.out.println("Quest Telah Dikerjakan");
        } else {
            isCompleted = true;
            System.out.println("Quest Telah Selesai");
        }
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return isCompleted;
    }
}
