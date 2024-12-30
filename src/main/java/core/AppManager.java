package core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import models.*;

public class AppManager {
    public static FirebaseApp firebaseApp;
    public static Firestore firestore;
    public static User currentUser = null;
    public static Community selectedCommunity = null;
    public static Quest selectedQuest = null;
    public static Event selectedEvent = null;
    public static List<Event> events = new ArrayList<>();
    public static List<Community> communities = new ArrayList<>();
    public static ArrayList<String> eventTitles = new ArrayList<>();
    public static ArrayList<Event> userEvents = new ArrayList<>();

    public static void initializeFirebase() throws IOException {
        FileInputStream serviceAccount = new FileInputStream("C:/connectify-telu-firebase-adminsdk.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        FirebaseOptions options = FirebaseOptions.builder().setCredentials(credentials).build();
        firebaseApp = FirebaseApp.initializeApp(options);
        firestore = FirestoreClient.getFirestore(firebaseApp);
        communityStartListening();
        eventStartListening();
    }

    public static void removeDocument(String path, String id) {
        try {
            DocumentReference docRef = firestore.collection(path).document(id);
            ApiFuture<WriteResult> result = docRef.delete();
            result.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static QuerySnapshot getQueryByFieldValue(String path, String fieldName, String value) {
        try {
            ApiFuture<QuerySnapshot> query = firestore.collection(path).whereEqualTo(fieldName, value).get();
            return query.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /// ------------------------------------
    /// User section
    /// ------------------------------------
    public static User createUserFromDoc(QueryDocumentSnapshot doc) {
        try {
            String displayName = doc.getString("displayname");
            String username = doc.getString("username");
            String type = doc.getString("type");
            String company = doc.getString("company");
            String encryptedPassword = doc.getString("password");
            String userId = doc.getString("id");
            assert encryptedPassword != null;
            String password = EncryptionUtils.decrypt(encryptedPassword, EncryptionUtils.getGlobalSecretKey());

            if (type.equals("MAHASISWA")) {
                Map<String, Boolean> questData = (Map<String, Boolean>)doc.get("quests");
                Map<Quest, Boolean> questMap = new HashMap<>();
                if (questData != null) {
                    for (Map.Entry<String, Boolean> entry : questData.entrySet()) {
                        Quest quest = getQuestById(entry.getKey());
                        if (quest != null) {
                            questMap.put(quest, entry.getValue());
                        }
                    }
                }

                Mahasiswa mahasiswa = new Mahasiswa(displayName, username, company, password);

                mahasiswa.setId(userId);
                mahasiswa.setQuests(questMap);

                List<String> achIds = (List<String>)doc.get("achievementIds");
                achIds.forEach(id -> {
                    Achievement ach = getAchievementById(id);
                    if (ach != null) {
                        mahasiswa.addAchievement(ach);
                    }
                });

                return mahasiswa;
            } else {
                User user = new User(displayName, username, type, company, password);
                user.setId(userId);
                return user;
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static boolean registerUser(User user) {
        try {
            Map<String, Object> userData = user.getStringObjectMap();
            DocumentReference docRef = firestore.collection("users").document(user.getId());
            ApiFuture<WriteResult> result = docRef.set(userData);
            result.get();
            return true;
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to register user to firestore: "+e.getMessage());
            throw new RuntimeException();
        }
    }

    public static User loginUser(String username, String password) {
        try {
            QuerySnapshot q = getQueryByFieldValue("users", "username", username);
            QueryDocumentSnapshot doc = q.getDocuments().getFirst();
            String encryptedPasswordStored = doc.getString("password");
            String encryptedPasswordInput = EncryptionUtils.encrypt(password, EncryptionUtils.getGlobalSecretKey());
            assert encryptedPasswordStored != null;
            if (encryptedPasswordStored.equals(encryptedPasswordInput)) {
                String id = doc.getString("id");
                String displayName = doc.getString("displayname");
                String type = doc.getString("type");
                String company = doc.getString("company");
                User loggedInUser = new User(displayName, username, type, company, password);
                loggedInUser.setId(id);
                return loggedInUser;
            }
            System.err.println("[ERROR] No matching user found or incorrect password.");
            return null;
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to login user from firestore: "+e.getMessage());
            return null;
        }
    }

    public static User getUserByUsername(String username) {
        try {
            QuerySnapshot q = getQueryByFieldValue("users", "username", username);
            QueryDocumentSnapshot doc = q.getDocuments().getFirst();
            return createUserFromDoc(doc);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static User getUserById(String userId) {
        try {
            QuerySnapshot q = getQueryByFieldValue("users", "id", userId);
            QueryDocumentSnapshot doc = q.getDocuments().getFirst();
            return createUserFromDoc(doc);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static void storeEventToUser(Event event, User user) {
        try {
            if (user.isMahasiswa()) {
                Mahasiswa mhs = (Mahasiswa) user;
                mhs.addEvent(event);

                Map<String, Object> updateData = new HashMap<>();
                List<String> eventIds = new ArrayList<>();
                for (Event e : mhs.getEvents()) {
                    eventIds.add(e.getId());
                }
                updateData.put("eventIds", eventIds);

                firestore.collection("users")
                        .document(user.getId())
                        .update(updateData);
            } else {
                System.err.println("[ERROR] User is not a mahasiswa.");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to add event to user: " + e.getMessage());
        }
    }

    public static void storeAchievementToMahasiswa(Achievement ach, Mahasiswa mhs) {
        try {
            mhs.addAchievement(ach);

            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("achievementIds", mhs.getAchievementIds());

            firestore.collection("users")
                     .document(mhs.getId())
                     .update(updateMap);

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to store achievement to mahasiswa." + e.getMessage());
        }
    }

    /// ------------------------------------
    /// Chat section
    /// ------------------------------------

    public static Chat createChatFromDoc(QueryDocumentSnapshot doc) {
        try {
            String id = doc.getString("id");
            String message = doc.getString("message");
            String senderId = doc.getString("sender");
            String type = doc.getString("type");
            User sender = getUserById(senderId);
            Timestamp timestamp = doc.getTimestamp("timestamp");

            if (type != null && type.equals("event")) {
                String eventId = doc.getString("eventId");
                EventChat eventChat = new EventChat(message, timestamp, sender);
                Event event = getEventById(eventId);
                eventChat.setEvent(event);
                return eventChat;
            }
            return new Chat(message, timestamp, sender, id);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
    public static Chat getChatById(String chatId) {
        try {
            QuerySnapshot q = getQueryByFieldValue("chats", "id", chatId);
            QueryDocumentSnapshot doc = q.getDocuments().getFirst();
            return createChatFromDoc(doc);
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to get chat by id: "+chatId);
            return null;
        }
    }

    public static void storeChatToDatabase(Chat chat) {
        try {
            Map<String, Object> chatData = chat.getStringObjectMap();
            DocumentReference docRef = firestore.collection("chats").document(chat.getId());
            ApiFuture<WriteResult> result = docRef.set(chatData);
            result.get();
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to send message: "+e.getMessage());
        }
    }

    public static void storeChatToDatabase(EventChat chat) {
        try {
            Map<String, Object> chatData = chat.getStringObjectMap();
            DocumentReference docRef = firestore.collection("chats").document(chat.getId());
            ApiFuture<WriteResult> result = docRef.set(chatData);
            result.get();
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to send message: "+e.getMessage());
        }
    }

    /// ------------------------------------
    /// Event section
    /// ------------------------------------

    private static Event createEventFromDoc(DocumentSnapshot doc) {
        String creatorId = doc.getString("creatorId");
        String description = doc.getString("description");
        String id = doc.getString("id");
        String title = doc.getString("title");

        Event event = new Event(title, description, id);
        event.setCreatorId(creatorId);

        // load mahasiswa
        List<String> mhsIds = (List<String>)doc.get("mahasiswaIds");
        assert mhsIds != null;

        mhsIds.forEach(mhsId -> {
            Mahasiswa mhs = (Mahasiswa) getUserById(mhsId);
            if (mhs != null) {
                event.addMahasiswa(mhs);
            }
        });

        // load quests
        List<String> questIds = (List<String>)doc.get("questsIds");
        assert questIds != null;

        questIds.forEach(questId -> {
            Quest quest = getQuestById(questId);
            if (quest != null) {
                event.addQuest(quest);
            }
        });

        // load communities
        List<String> communityIds = (List<String>)doc.get("communityIds");
        assert communityIds != null;

        communityIds.forEach(comId -> {
            Community com = getCommunityById(comId);
            if (com != null) {
                event.addCommunity(com);
            }
        });

        return event;
    }

    private static void eventStartListening() {
        firestore.collection("events").addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                System.err.println("[ERROR]  Listening failed: "+error);
                return;
            }

            if (snapshot != null) {
                for (DocumentChange change : snapshot.getDocumentChanges()) {
                    String docId = change.getDocument().getId();
                    // find if the document ID matches a local community
                    Event localEvent = events.stream()
                            .filter(c -> c.getId().equals(docId))
                            .findFirst()
                            .orElse(null);

                    // handle based on the type of change
                    switch (change.getType()) {
                        case ADDED -> {
                            Event event = createEventFromDoc(change.getDocument());
                            events.add(event);
                            eventTitles.add(event.getTitle());
                        }
                        case MODIFIED -> {
                            if (localEvent != null) {
                                try {
                                    CollectionReference ref = firestore.collection("events");
                                    DocumentReference docRef = ref.document(localEvent.getId());
                                    docRef.update("title", localEvent.getTitle());
                                    docRef.update("description", localEvent.getDescription());
                                    docRef.update("mahasiswaIds", localEvent.getMahasiswaIds());
                                    docRef.update("communityIds", localEvent.getCommunityIds());
                                } catch (Exception e) {
                                    System.err.println("[ERROR] Failed to update event: "+e.getMessage());
                                }
                            }
                        }
                        case REMOVED -> {
                            if (localEvent != null) {
                                events.remove(localEvent);
                            }
                        }
                    }
                }
            }
        });
    }

    public static void storeQuestToEvent(Quest quest, Event event) {
        try {
            storeQuestToDatabase(quest);
            event.addQuest(quest);

            Map<String, Object> updateData = new HashMap<>();
            updateData.put("questIds", event.getQuestIDs());

            firestore.collection("events")
                    .document(event.getId())
                    .update(updateData);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void storeMahasiswaToEvent(Mahasiswa mhs, Event event) {
        try {
            event.getQuests().forEach(mhs::addQuest);
            event.addMahasiswa(mhs);

            Map<String, Boolean> questMap = new HashMap<>();
            for (Map.Entry<Quest, Boolean> entry : mhs.getQuests().entrySet()) {
                questMap.put(entry.getKey().getId(), entry.getValue());
            }

            Map<String, Object> mahasiswaUpdateData = new HashMap<>();
            mahasiswaUpdateData.put("quests", questMap);
            mahasiswaUpdateData.put("achievementIds", new ArrayList<String>());

            firestore.collection("users")
                    .document(mhs.getId())
                    .update(mahasiswaUpdateData);

            Map<String, Object> eventUpdateData = new HashMap<>();
            eventUpdateData.put("mahasiswaIds", event.getMahasiswaIds());

            firestore.collection("events")
                    .document(event.getId())
                    .update(eventUpdateData);

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to add mahasiswa to event!");
        }
    }


    public static void storeEventToDatabase(Event event) {
        try {
            Map<String, Object> comData = event.getStringObjectMap();
            DocumentReference docRef = firestore.collection("events").document(event.getId());
            ApiFuture<WriteResult> result = docRef.set(comData);
            result.get();
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to create event");
            throw new RuntimeException();
        }
    }

    public static Event getEventByTitle(String eventTitle) {
        try {
            QuerySnapshot q = getQueryByFieldValue("events", "title", eventTitle);
            QueryDocumentSnapshot doc = q.getDocuments().getFirst();
            return createEventFromDoc(doc);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static Event getEventById(String eventId) {
        try {
            QuerySnapshot q = getQueryByFieldValue("events", "id", eventId);
            QueryDocumentSnapshot doc = q.getDocuments().getFirst();
            return createEventFromDoc(doc);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static void updateEvent(Event event) {
        try {
            CollectionReference ref = firestore.collection("events");
            DocumentReference docRef = ref.document(event.getId());

            docRef.update("title", event.getTitle());
            docRef.update("description", event.getDescription());
            docRef.update("mahasiswaIds", event.getCommunityIds());
            docRef.update("questIds", event.getQuestIDs());
            docRef.update("communityIds", event.getCommunityIds());

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to update event: "+e.getMessage());
        }
    }


    /// ------------------------------------
    /// Achievement section
    /// ------------------------------------

    public static Achievement createAchievementFromDoc(QueryDocumentSnapshot doc) {
        try {
            String id = doc.getString("id");
            String name = doc.getString("name");

            Achievement ach = new Achievement(name, id);
            ach.setTags((List<String>) doc.get("tags"));

            return ach;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static Achievement getAchievementById(String achId) {
        try {
            QuerySnapshot q = getQueryByFieldValue("achievements", "id", achId);
            QueryDocumentSnapshot doc = q.getDocuments().getFirst();
            return createAchievementFromDoc(doc);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static void storeAchievementToDatabase(Achievement ach) {
        try {
            Map<String, Object> achData = ach.getStringObjectMap();
            DocumentReference docRef = firestore.collection("achievements").document(ach.getId());
            ApiFuture<WriteResult> result = docRef.set(achData);
            result.get();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    /// ------------------------------------
    /// Quests section
    /// ------------------------------------

    public static Quest createQuestFromDoc(QueryDocumentSnapshot doc) {
        try {
            String id = doc.getString("id");
            String title = doc.getString("title");
            String description = doc.getString("description");
            Quest quest = new Quest(title, description, id);

            List<String> achIds = (List<String>)doc.get("achievementIds");
            assert achIds != null;
            achIds.forEach(achId -> {
                Achievement ach = getAchievementById(achId);
                if (ach != null) {
                    quest.addAchievement(ach);
                }
            });

            return quest;
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static void storeQuestToDatabase(Quest quest) {
        try {
            Map<String, Object> comData = quest.getStringObjectMap();
            DocumentReference docRef = firestore.collection("quests").document(quest.getId());
            ApiFuture<WriteResult> result = docRef.set(comData);
            result.get();
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to create quest");
            throw new RuntimeException();
        }
    }

    public static Quest getQuestById(String questId) {
        try {
            QuerySnapshot q = getQueryByFieldValue("quests", "id", questId);
            QueryDocumentSnapshot doc = q.getDocuments().getFirst();
            return createQuestFromDoc(doc);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static Quest getQuestByName(String questName) {
        try {
            QuerySnapshot q = getQueryByFieldValue("quests", "title", questName);
            QueryDocumentSnapshot doc = q.getDocuments().getFirst();
            return createQuestFromDoc(doc);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static void storeAchievementToQuest(Achievement ach, Quest quest) {
        try {
            storeAchievementToDatabase(ach);
            quest.addAchievement(ach);

            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("achievementIds", quest.getAchievementIds());

            firestore.collection("quests")
                    .document(quest.getId())
                    .update(updateMap);

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to add achievement to quest: " + e.getMessage());
        }
    }

    /// ------------------------------------
    /// Communities section
    /// ------------------------------------
    private static void communityStartListening() {
        firestore.collection("communities").addSnapshotListener((snapshot, error) -> {
           if (error != null) {
               System.err.println("[ERROR]  Listening failed: "+error);
               return;
           }

           if (snapshot != null) {
               for (DocumentChange change : snapshot.getDocumentChanges()) {
                   String docId = change.getDocument().getId();
                   // find if the document ID matches a local community
                   Community localCommunity = communities.stream()
                           .filter(c -> c.getId().equals(docId))
                           .findFirst()
                           .orElse(null);

                   // handle based on the type of change
                   switch (change.getType()) {
                       case ADDED -> {
                           Community newCommunity = createCommunityFromDoc(change.getDocument());
                           communities.add(newCommunity);
                       }
                       case MODIFIED -> {
                           if (localCommunity != null) {
                               communityUpdateLocal(localCommunity, change.getDocument());
                           }
                       }
                       case REMOVED -> {
                           if (localCommunity != null) {
                               communities.remove(localCommunity);
                           }
                       }
                   }
               }
           }
        });
    }

    public static void storeCommunityToDatabase(Community community) {
        try {
            Map<String, Object> comData = community.getStringObjectMap();
            DocumentReference docRef = firestore.collection("communities").document(community.getId());
            ApiFuture<WriteResult> result = docRef.set(comData);
            result.get();
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to create community");
            throw new RuntimeException();
        }
    }

    private static Community createCommunityFromDoc(DocumentSnapshot doc) {
        String name = doc.getString("name");
        String ownerId = doc.getString("ownerID");
        String id = doc.getString("id");
        Community com = new Community(name, ownerId);

        // load chats
        ArrayList<String> chatIds = (ArrayList<String>)doc.get("chatIDs");
        assert chatIds != null;
        chatIds.forEach(x -> {
            Chat c = getChatById(x);
            if (c != null) {
                com.addChat(c);
            }
        });

        // load mahasiswa
        ArrayList<String> mhsIds = (ArrayList<String>)doc.get("mahasiswaIDs");
        assert mhsIds != null;
        mhsIds.forEach(x -> {
            Mahasiswa mhs = (Mahasiswa) getUserById(x);
            if (mhs != null) {
                com.addMahasiswa(mhs);
            }
        });

        // load events
        ArrayList<String> eventIds = (ArrayList<String>)doc.get("eventIDs");
        assert eventIds != null;
        eventIds.forEach(x -> {
            Event event = getEventById(x);
            if (event != null) {
                com.addEvent(event);
            }
        });

        com.setId(id);
        return com;
    }

    public static void storeEventToCommunity(Event event, Community community) {
        try {
            storeEventToDatabase(event);
            community.addEvent(event);

            Map<String, Object> updateData = new HashMap<>();
            updateData.put("eventIDs", community.getEventIds());

            firestore.collection("communities")
                    .document(community.getId())
                    .update(updateData);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    public static void storeChatToCommunity(Chat chat, Community community) {
        try {
            community.addChat(chat);
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("chatIDs", community.getChatIds());
            firestore.collection("communities")
                    .document(community.getId())
                    .update(updateData);

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to add chat to community");
        }
    }

    public static Community getCommunityByName(String comName) {
        try {
            QuerySnapshot q = getQueryByFieldValue("communities", "name", comName);
            QueryDocumentSnapshot doc = q.getDocuments().getFirst();
            return createCommunityFromDoc(doc);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static Community getCommunityById(String comId) {
        try {
            QuerySnapshot q = getQueryByFieldValue("communities", "id", comId);
            QueryDocumentSnapshot doc = q.getDocuments().getFirst();
            return createCommunityFromDoc(doc);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    private static void communityUpdateLocal(Community community, DocumentSnapshot doc) {
        String name = doc.getString("name");
        String id = doc.getString("id");

        ArrayList<String> chatIDs = (ArrayList<String>)doc.get("chatIDs");
        ArrayList<Chat> chats = new ArrayList<>();

        assert chatIDs != null;
        chatIDs.forEach(x -> {
            Chat c = getChatById(x);
            if (c != null) {
                chats.add(c);
            }
        });

        community.setChats(chats);
        community.setName(name);
        community.setId(id);
    }
}
