package models;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event extends ModelBase {
    private String title;
    private List<String> mahasiswaIds;
    private List<String> questsIds;
    private String description;
    private String creatorId;
    private String communityId;

    public Event(String title) {
        super();
        this.title = title;
        this.mahasiswaIds = new ArrayList<>();
        this.questsIds = new ArrayList<>();
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public final String getCreatorId() {
        return creatorId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addMahasiswa(Mahasiswa mhs) {
        mahasiswaIds.add(mhs.getId());
    }

    public void addQuest(Quest quest) {
        questsIds.add(quest.getId());
    }

    public void setMahasiswaIds(List<String> mahasiswaIds) {
        this.mahasiswaIds = mahasiswaIds;
    }

    public void setQuestsIds(List<String> questsIds) {
        this.questsIds = questsIds;
    }

    public List<String> getQuestIDs() {
        return questsIds;
    }

    public List<String> getMahasiswaIDs() {
        return mahasiswaIds;
    }

    public final String getTitle() {
        return title;
    }

    @NotNull
    public Map<String, Object> getStringObjectMap() {
        Map<String, Object> stringObj = new HashMap<>();
        stringObj.put("id", id);
        stringObj.put("title", title);
        stringObj.put("description", description);
        stringObj.put("mahasiswaIds", mahasiswaIds);
        stringObj.put("questsIds", questsIds);
        stringObj.put("creatorId", creatorId);
        stringObj.put("communityId", communityId);
        return stringObj;
    }
}
