package models;

import core.StringUtils;

public class ModelBase {
    protected String id;

    protected ModelBase() {
        this.id = StringUtils.generateUUID();
    }

    protected ModelBase(String id) {
        this.id = id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Quest[] getQuests() {
    }
}
