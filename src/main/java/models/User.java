package models;

public class User extends ModelBase {
    protected String name;

    public User(String name) {
        super();
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
