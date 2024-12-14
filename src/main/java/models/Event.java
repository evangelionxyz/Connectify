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
    private String community;
    private String description;
    private String creatorId;
    private List<String> communityIds;

    public Event(String title, String community) {
        super();
        this.title = title;
        this.community = community;
        this.mahasiswaIds = new ArrayList<>();
        this.questsIds = new ArrayList<>();
        this.communityIds = new ArrayList<>();
    }

    public String getCommunity() {
        return community;
    }


    public void setCommunity(String community) {
        this.community = community;
    }

    public void addCommunityId(String communityId) {
        this.communityIds.add(communityId);
    }

    public void setCommunityIds(List<String> communityIds) {
        this.communityIds = communityIds;
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

    public void addMahasiswa(String mhsId) {
        mahasiswaIds.add(mhsId);
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
        stringObj.put("communityIds", communityIds);
        return stringObj;
    }
}
