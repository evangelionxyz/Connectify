package core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import models.Chat;
import models.Community;
import models.User;

public class AppManager {
    public static FirebaseApp firebaseApp;
    public static Firestore firestore;

    public static User currentUser = null;

    public static void sendMessageToDb(Community activeCommunity, Chat chat) {
        try {
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("content", chat.getMessage());
            messageData.put("timestamp", FieldValue.serverTimestamp());
            messageData.put("sender", chat.getSenderId());

            // communities -> id -> chats -> document
            DocumentReference docRef = firestore.collection("communities")
                    .document(activeCommunity.getId())
                    .collection("chats")
                    .document();

            ApiFuture<WriteResult> result = docRef.set(messageData);

            // wait for write to complete
            WriteResult writeResult = result.get();

            DocumentSnapshot snapshot = docRef.get().get(); // get written document
            chat.setTimestamp(snapshot.getTimestamp("timestamp"));
            activeCommunity.addChat(chat);

        } catch (Exception e) {
            System.err.println("[ERROR] Failed to send message: "+e.getMessage());
        }
    }

    public static boolean registerUser(User user) {
        try {
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", user.getUsername());
            userData.put("displayname", user.getName());
            userData.put("type", user.getType());
            userData.put("company", user.getCompany());
            userData.put("password", user.getPassword());
            userData.put("id", user.getId());

            // user -> document
            DocumentReference docRef = firestore.collection("users")
                    .document(user.getId());

            ApiFuture<WriteResult> result = docRef.set(userData);

            // wait for write
            WriteResult writeResult = result.get();

            return true;
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to register user to firestore: "+e.getMessage());
            return false;
        }
    }

    public static User loginUser(String username, String password) {
        try {
            Query query = firestore.collection("users")
                    .whereEqualTo("username", username)
                    .whereEqualTo("password", password);

            ApiFuture<QuerySnapshot> querySnapshot = query.get();

            for (QueryDocumentSnapshot doc : querySnapshot.get().getDocuments()) {
                String name = doc.getString("displayname");
                String type = doc.getString("type");
                String company = doc.getString("company");
                String id = doc.getString("id");

                User newUser = new User(name, username, type, company, password);
                newUser.setId(id);
                return newUser;
            }

            return null;
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to login user from firestore: "+e.getMessage());
            return null;
        }
    }

    public static User getUser(String userId) {
        return null;
    }

    public static boolean registerCommunity(Community com) {
        try {
            return true;
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to register user to firestore");
            return false;
        }
    }

    public static void initializeFirebase() throws IOException {
        // "D:/Dev/connectify-telu-firebase-adminsdk.json"
        FileInputStream serviceAccount = new FileInputStream("D:/Dev/connectify-telu-firebase-adminsdk.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials).build();
        firebaseApp = FirebaseApp.initializeApp(options);
        firestore = FirestoreClient.getFirestore(firebaseApp);
    }

}
