package models;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Community extends ModelBase {
    private String name;
    private List<Mahasiswa> mahasiswa;
    private List<Chat> chats;
    private final List<Event> events;

    private final List<String> mahasiswaIds;
    private final List<String> chatIds;
    private final List<String> eventIds;

    private String ownerId;

    public Community(String name, String ownerId) {
        super();
        this.name = name;
        this.mahasiswa = new ArrayList<>();
        this.chats = new ArrayList<>();
        this.events = new ArrayList<>();

        this.mahasiswaIds = new ArrayList<>();
        this.chatIds = new ArrayList<>();
        this.eventIds = new ArrayList<>();

        this.ownerId = ownerId;
    }

    public Community(String name, String ownerId, String id) {
        super(id);
        this.name = name;
        this.mahasiswa = new ArrayList<>();
        this.chats = new ArrayList<>();
        this.events = new ArrayList<>();

        this.mahasiswaIds = new ArrayList<>();
        this.chatIds = new ArrayList<>();
        this.eventIds = new ArrayList<>();

        this.ownerId = ownerId;
    }

    public void addEvent(Event event) {
        events.add(event);
        eventIds.add(event.id);
    }

    // remove by id
    public void removeEvent(String eventId) {
        if (events.removeIf((event -> event.id.equals(eventId)))) {
            eventIds.removeIf(id -> id.equals(eventId));
        }
    }

    public void removeEvent(Event event) {
        events.remove(event);
        eventIds.removeIf(id -> id.equals(event.id));
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMahasiswa(List<Mahasiswa> mhs) {
        mahasiswa = mhs;
        mahasiswa.forEach(x -> {
            mahasiswaIds.add(x.id);
        });
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }

    public void addMahasiswa(Mahasiswa mhs) {
        if (!mahasiswa.contains(mhs)) {
            mahasiswa.add(mhs);
            mahasiswaIds.add(mhs.id);
        }
    }

    public final List<Mahasiswa> getMahasiswa() {
        return mahasiswa;
    }

    public void addChat(Chat chat) {
        chats.add(chat);
        chatIds.add(chat.getId());
    }

    public void removeChat(String chatId) {
        if (chats.removeIf(chat -> chat.id.equals(chatId))) {
            chatIds.remove(chatId);
        }
    }

   public void removeChat(Chat chat) {
        chats.remove(chat);
        chatIds.removeIf(id -> id.equals(chat.id));
   }

    public final List<Chat> getChats() {
        return chats;
    }

    public List<String> getChatIds() {
        return chatIds;
    }

    public List<String> getMahasiswaIds() {
        return mahasiswaIds;
    }

    public List<String> getEventIds() {
        return eventIds;
    }

    @NotNull
    public Map<String, Object> getStringObjectMap() {
        Map<String, Object> comData = new HashMap<>();
        comData.put("name", name);
        comData.put("id", id);
        comData.put("ownerID", ownerId);
        comData.put("mahasiswaIDs", mahasiswaIds);
        comData.put("eventIDs", eventIds);
        comData.put("chatIDs", chatIds);
        return comData;
    }

    @Override
    public String toString() {
        return String.format("Name %s: ", name);
    }

}
