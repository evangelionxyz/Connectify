package models;

public class ModelBase {
    protected String id;

    protected ModelBase() {
        this.id = Utils.generateUUID();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
