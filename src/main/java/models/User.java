package models;

import core.EncryptionUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class User extends ModelBase {
    protected String password;
    protected String name;
    protected String username;
    protected String type;
    protected String company;

    public User(String name, String username, String type, String company) {
        super();
        this.name = name;
        this.username = username;
        this.type = type;
        this.company = company;
    }

    public User(String name, String username, String type, String company, String password) {
        super();
        this.name = name;
        this.username = username;
        this.type = type;
        this.company = company;
        this.password = password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public String getCompany() {
        return company;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getType() {
        return type;
    }

    @NotNull
    public Map<String, Object> getStringObjectMap() {
        Map<String, Object> userData = new HashMap<>();
        userData.put("username", username);
        userData.put("displayname", name);
        userData.put("type", type);
        userData.put("company", company);

        // encrypt the password before storing
        String encryptedPassword;
        try {
            encryptedPassword = EncryptionUtils.encrypt(password, EncryptionUtils.getGlobalSecretKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        userData.put("password", encryptedPassword);
        userData.put("id", id);
        return userData;
    }
}
