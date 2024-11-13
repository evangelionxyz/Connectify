package models;

public class HRD extends User {
    private String company;

    public HRD(String name, String username, String company) {
        super(name, "hrd", username, company);
        this.company = company;
    }
}
