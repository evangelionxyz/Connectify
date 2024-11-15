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
import models.*;

import javax.crypto.SecretKey;

public class AppManager {
    public static FirebaseApp firebaseApp;
    public static Firestore firestore;

    public static User currentUser = null;

    public static void sendMessageToDb(Community activeCommunity, Chat chat) {
        try {
            Map<String, Object> messageData = new HashMap<>();
            messageData.put("content", chat.getMessage());
            messageData.put("timestamp", FieldValue.serverTimestamp());
            messageData.put("sender", chat.getSender());

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

    public static boolean registerUser(User user, SecretKey key) {
        try {
            Map<String, Object> userData = new HashMap<>();
            userData.put("username", user.getUsername());
            userData.put("displayname", user.getName());
            userData.put("type", user.getType());
            userData.put("company", user.getCompany());

            // encrypt the password before storing
            String encryptedPassword = EncryptionUtils.encrypt(user.getPassword(), key);
            userData.put("password", encryptedPassword);
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
                    .limit(1);

            ApiFuture<QuerySnapshot> querySnapshot = query.get();
            QuerySnapshot snapshot = querySnapshot.get();

            if (snapshot.isEmpty()) {
                System.err.println("[Error] No user found with username: "+username);
                return null;
            }

            QueryDocumentSnapshot userDoc = snapshot.getDocuments().get(0);

            String storedHash = userDoc.getString("password");

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

    private static String getDecryptedPassword(String encryptedPassword, SecretKey key) {
        try {
            return EncryptionUtils.decrypt(encryptedPassword, key);
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to decrypt password: "+e.getMessage());
            return null;
        }
    }

    public static User getUserById(String userId) {
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
        FileInputStream serviceAccount = new FileInputStream("C:/connectify-telu-firebase-adminsdk.json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials).build();
        firebaseApp = FirebaseApp.initializeApp(options);
        firestore = FirestoreClient.getFirestore(firebaseApp);
    }

}
