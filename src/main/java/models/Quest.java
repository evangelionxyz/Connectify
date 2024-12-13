package models;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

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

    public void setCompletion(boolean completion) {
        this.isCompleted = completion;
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

    @NotNull
    public Map<String, Object> getStringObjectMap() {
        Map<String, Object> stringObj = new HashMap<>();
        stringObj.put("id", id);
        stringObj.put("title", title);
        stringObj.put("description", description);
        stringObj.put("isCompleted", isCompleted);
        return stringObj;
    }
}
