package models;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event extends ModelBase {
    private String title;
    private String description;
    private String creatorId;

    private final List<Mahasiswa> mahasiswa;
    private final List<Quest> quests;
    private final List<Community> communities;

    private final List<String> mahasiswaIds;
    private final List<String> questsIds;
    private final List<String> communityIds;

    public Event(String title, String description) {
        super();
        this.title = title;
        this.description = description;

        this.mahasiswa = new ArrayList<>();
        this.quests = new ArrayList<>();
        this.communities = new ArrayList<>();

        this.mahasiswaIds = new ArrayList<>();
        this.questsIds = new ArrayList<>();
        this.communityIds = new ArrayList<>();
    }

    public Event(String title, String description, String id) {
        super(id);
        this.title = title;
        this.description = description;

        this.mahasiswa = new ArrayList<>();
        this.quests = new ArrayList<>();
        this.communities = new ArrayList<>();

        this.mahasiswaIds = new ArrayList<>();
        this.questsIds = new ArrayList<>();
        this.communityIds = new ArrayList<>();
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public final String getTitle() {
        return title;
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

    public void addCommunity(Community com) {
        if (!communities.contains(com)) {
            communities.add(com);
            communityIds.add(com.id);
        }
    }

    public List<String> getCommunityIds() {
        return communityIds;
    }

    public List<Community> getCommunities() {
        return communities;
    }

    public void addQuest(Quest quest) {
        if (!quests.contains(quest)) {
            quests.add(quest);
            questsIds.add(quest.getId());
        }
    }

    public List<String> getQuestIDs() {
        return questsIds;
    }

    public List<Quest> getQuests() {
        return quests;
    }

    public void addMahasiswa(Mahasiswa mhs) {
        if (!mahasiswa.contains(mhs)) {
            mahasiswa.add(mhs);
            mahasiswaIds.add(mhs.getId());
        }
    }

    public void addMahasiswaId(String mhsId) {
        mahasiswaIds.add(mhsId);
    }

    public List<String> getMahasiswaIds() {
        return mahasiswaIds;
    }

    public List<Mahasiswa> getMahasiswa() {
        return mahasiswa;
    }

    @NotNull
    public Map<String, Object> getStringObjectMap() {
        Map<String, Object> stringObj = new HashMap<>();
        stringObj.put("id", id);
        stringObj.put("title", title);
        stringObj.put("description", description);
        stringObj.put("mahasiswaIds", mahasiswaIds);
        stringObj.put("questsIds", questsIds);
        stringObj.put("communityIds", communityIds);

        stringObj.put("creatorId", creatorId);
        return stringObj;
    }
}
