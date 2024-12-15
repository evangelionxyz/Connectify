package models;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Quest extends ModelBase {
    private String title;
    private String description;

    private final List<String> achievementIds;
    private final List<Achievement> achievements;

    public Quest(String title, String description) {
        super();

        this.title = title;
        this.description = description;

        achievements = new ArrayList<>();
        achievementIds = new ArrayList<>();
    }

    public Quest(String title, String description, String id) {
        super(id);

        this.title = title;
        this.description = description;

        achievements = new ArrayList<>();
        achievementIds = new ArrayList<>();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addAchievement(Achievement ach) {
        if (!achievements.contains(ach)) {
            achievements.add(ach);
            achievementIds.add(ach.id);
        }
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public List<Achievement> getAchievements() {
        return achievements;
    }

    public List<String> getAchievementIds() {
        return achievementIds;
    }

    @NotNull
    public Map<String, Object> getStringObjectMap() {
        Map<String, Object> stringObj = new HashMap<>();
        stringObj.put("id", id);
        stringObj.put("title", title);
        stringObj.put("description", description);
        stringObj.put("achievementIds", achievementIds);
        return stringObj;
    }
}
