package models;

public class User {
    private String id;
    private String name;

    User(String name) {
        this.name = name;
        System.out.printf("Test %d\n", 10);
    }

}
