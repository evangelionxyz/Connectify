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
    public static Event selectedEvent = null;
    public static List<Community> communities = new ArrayList<>();
    public static List<Event> events = new ArrayList<>();

    public static void initializeFirebase() throws IOException {
        FileInputStream serviceAccount = new FileInputStream("C:/connectify-telu-firebase-adminsdk.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials).build();
        firebaseApp = FirebaseApp.initializeApp(options);
        firestore = FirestoreClient.getFirestore(firebaseApp);

        // initialize communities
        communityStartListening();
    }

    public static boolean registerUser(User user) {
        try {
            Map<String, Object> userData = user.getStringObjectMap();
            // get collection's document user -> document by id
            DocumentReference docRef = firestore.collection("users").document(user.getId());
            // write to firestore
            ApiFuture<WriteResult> result = docRef.set(userData);
            // wait for write
            WriteResult writeResult = result.get();
            return true;
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to register user to firestore: "+e.getMessage());
            throw new RuntimeException();
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

    public static User loginUser(String username, String password) {
        try {
            QuerySnapshot q = getQueryByFieldValue("users", "username", username);
            QueryDocumentSnapshot doc = q.getDocuments().getFirst();
            // retrieve encrypted password from Firestore
            String encryptedPasswordStored = doc.getString("password");
            // encrypt the input password for comparison
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
            System.err.println("[ERROR] No matching user found or incorect password.");
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

            String displayName = doc.getString("displayname");
            String type = doc.getString("type");
            String company = doc.getString("company");
            String encryptedPassword = doc.getString("password");
            String userId = doc.getString("id");
            assert encryptedPassword != null;
            String password = EncryptionUtils.decrypt(encryptedPassword, EncryptionUtils.getGlobalSecretKey());
            User user = new User(displayName, username, type, company, password);
            user.setId(userId);
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    public static User getUserById(String userId) {
        try {
            QuerySnapshot q = getQueryByFieldValue("users", "id", userId);
            QueryDocumentSnapshot doc = q.getDocuments().getFirst();
            String username = doc.getString("username");
            String displayName = doc.getString("displayname");
            String type = doc.getString("type");
            String company = doc.getString("company");
            String encryptedPassword = doc.getString("password");
            assert encryptedPassword != null;
            String password = EncryptionUtils.decrypt(encryptedPassword, EncryptionUtils.getGlobalSecretKey());
            User user = new User(displayName, username, type, company, password);
            user.setId(userId);
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    public static Chat getChatById(String chatId) {
        try {
            QuerySnapshot q = getQueryByFieldValue("chats", "id", chatId);
            QueryDocumentSnapshot doc = q.getDocuments().getFirst();
            String message = doc.getString("message");
            String senderId = doc.getString("sender");
            User sender = getUserById(senderId);
            Timestamp timestamp = doc.getTimestamp("timestamp");
            Chat chat = new Chat(message, timestamp, sender);
            chat.setId(chatId);
            return chat;
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to get chat by id: "+chatId);
            return null;
        }
    }

    public static void storeChatToDatabase(Chat chat) {
        try {
            Map<String, Object> chatData = chat.getStringObjectMap();
            // chats -> chat id
            DocumentReference docRef = firestore.collection("chats").document(chat.getId());
            ApiFuture<WriteResult> result = docRef.set(chatData);
            // wait for write to complete
            WriteResult writeResult = result.get();
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to send message: "+e.getMessage());
        }
    }

    public static void addChatToCommunity(Community community, Chat chat) {
        try {
            storeChatToDatabase(chat);
            community.addChat(chat);

            Map<String, Object> updateData = new HashMap<>();
            updateData.put("chatIDs", community.getChatIDs());

            firestore.collection("communities")
                    .document(community.getId())
                    .update(updateData);

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to add chat to community");
            throw new RuntimeException(e);
        }
    }

    /// ------------------------------------
    /// Event section
    /// ------------------------------------

    public static void storeEventToDatabase(Event event) {
        try {
            Map<String, Object> comData = event.getStringObjectMap();
            DocumentReference docRef = firestore.collection("events").document(event.getId());
            ApiFuture<WriteResult> result = docRef.set(comData);
            WriteResult writeResult = result.get();
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to create event");
            throw new RuntimeException();
        }
    }

    public static void removeEventFromDatabase(Event event) {
        try {
            DocumentReference docRef = firestore.collection("events").document(event.getId());
            ApiFuture<WriteResult> result = docRef.delete();
            WriteResult writeResult = result.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Event getEventById(String eventId) {
        try {
            QuerySnapshot q = getQueryByFieldValue("events", "id", eventId);
            QueryDocumentSnapshot doc = q.getDocuments().getFirst();

            String title = doc.getString("title");

            Event event = new Event(title);
            event.setId(eventId);
            return event;
        } catch (Exception e) {
            throw new RuntimeException(e);
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
                           Community newCommunity = communityCreateFromDocument(change.getDocument());
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
            WriteResult writeResult = result.get();
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to create community");
            throw new RuntimeException();
        }
    }

    public static void removeCommunityFromDatabase(Community community) {
        try {
            DocumentReference docRef = firestore.collection("communities").document(community.getId());
            ApiFuture<WriteResult> result = docRef.delete();
            WriteResult writeResult = result.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Community communityCreateFromDocument(DocumentSnapshot doc) {
        String name = doc.getString("name");
        String ownerId = doc.getString("ownerID");
        String id = doc.getString("id");
        User owner = getUserById(ownerId);
        Community com = new Community(name, owner);

        // load chats
        ArrayList<String> chats = (ArrayList<String>)doc.get("chatIDs");
        assert chats != null;
        chats.forEach(x -> {
            Chat c = getChatById(x);
            if (c != null) {
                com.addChat(c);
            }
        });
        com.setId(id);
        return com;
    }

    private static void communityUpdateLocal(Community community, DocumentSnapshot doc) {
        String name = doc.getString("name");
        String id = doc.getString("id");

        // load chats
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
