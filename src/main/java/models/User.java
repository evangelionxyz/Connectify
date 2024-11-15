package models;

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
}
