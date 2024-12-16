package models;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Achievement extends ModelBase {
    private String name;
    List<String> tags;

    public Achievement(String name, List<String> tags) {
        super();
        this.name = name;
        this.tags = tags;
    }

    public Achievement(String name, String id) {
        super(id);

        this.name = name;
        this.tags = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void addTags(String tag) {
        if (!tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public void removeTags(String tag)  {
        tags.remove(tag);
    }

    @NotNull
    public Map<String, Object> getStringObjectMap() {
        Map<String, Object> stringObj = new HashMap<>();
        stringObj.put("id", id);
        stringObj.put("name", name);
        stringObj.put("tags", tags);
        return stringObj;
    }

    public boolean isExists(String tag) {
        return tags.contains(tag);
    }
}
