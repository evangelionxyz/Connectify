package models;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Community extends ModelBase {
    private String name;
    private List<Mahasiswa> listMahasiswa;
    private List<Quest> listQuest;
    private List<Chat> listChat;
    private User owner;

    public Community(String name, User owner) {
        super();
        this.name = name;
        this.listMahasiswa = new ArrayList<>();
        this.listQuest = new ArrayList<>();
        this.listChat = new ArrayList<>();
        this.owner = owner;
    }

    public Community(String name, User owner, String id) {
        super(id);
        this.name = name;
        this.listMahasiswa = new ArrayList<>();
        this.listQuest = new ArrayList<>();
        this.listChat = new ArrayList<>();
        this.owner = owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMahasiswa(ArrayList<Mahasiswa> mhs) {
        this.listMahasiswa = mhs;
    }

    public void setChats(ArrayList<Chat> chats) {
        this.listChat = chats;
    }

    public void setQuests(ArrayList<Quest> quests) {
        this.listQuest = quests;
    }

    public void addMahasiswa(Mahasiswa mhs) {
        if (!listMahasiswa.contains(mhs)) {
            listMahasiswa.add(mhs);
        }
    }

    public void addChat(Chat chat) {
        listChat.add(chat);
    }

    public boolean isMahasiswaExists(String id){
        for (Mahasiswa mhs : listMahasiswa) {
            if (Objects.equals(mhs.getId(), id)) {
                return true;
            }
        }
        return false;
    }

    public void addQuest(Quest quest) {
        listQuest.add(quest);
    }

    public void setQuestToMahasiswa(Mahasiswa mhs, Quest quest) {
        if (isMahasiswaExists(mhs.getId())) {
            mhs.addQuest(quest);
        }
        System.out.println("Mahasiswa dengan NIM:" + mhs.getId() + "tidak ditemukan");
    }

    public final List<Mahasiswa> getMahasiswa() {
        return listMahasiswa;
    }

    public final List<Quest> getQuests() {
        return listQuest;
    }

    public final List<Chat> getChats() {
        return listChat;
    }

    public List<String> getQuestIDs() {
        ArrayList<String> questIds = new ArrayList<>();
        listQuest.forEach(x -> {
            questIds.add(x.getId());
        });
        return questIds;
    }

    public List<String> getChatIDs() {
        ArrayList<String> chatIds = new ArrayList<>();
        listChat.forEach(x -> {
            chatIds.add(x.getId());
        });
        return chatIds;
    }

    public List<String> getMahasiswaIDs() {
        ArrayList<String> mhsIds = new ArrayList<>();
        listMahasiswa.forEach(x -> {
            mhsIds.add(x.getId());
        });
        return mhsIds;
    }

    @NotNull
    public Map<String, Object> getStringObjectMap() {
        Map<String, Object> comData = new HashMap<>();
        comData.put("name", name);
        comData.put("id", id);

        if (owner != null) {
            comData.put("ownerID", owner.getId());
        }

        comData.put("mahasiswaIDs", getMahasiswaIDs());
        comData.put("questIDs", getQuestIDs());
        comData.put("chatIDs", getChatIDs());

        return comData;
    }

    @Override
    public String toString() {
        return String.format("Name %s: ", name);
    }

}
