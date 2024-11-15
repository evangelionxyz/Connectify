package models;

import java.util.ArrayList;
import java.util.List;

public class Achievement extends ModelBase {
    private String name;
    List<String> tags;

    public Achievement(String name, List<String> tags) {
        super();
        this.name = name;
        this.tags = new ArrayList<>(tags);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public boolean isExists(String tag) {
       for (String name : tags) {
           if (name.equals(tag)) {
               return true;
           }
       }
       return false;
    }

    public void addTags(String tag) {
        if (!isExists(tag)) {
            tags.add(tag);
            System.out.println("berhasil menambahkan tag");
        }else {
            System.out.println("tag sudah ada tidak dapat menambahkan");
        }
    }

    public void removeTags(String tag)  {
        if (isExists(tag)) {
            tags.remove(tag);
            System.out.println("berhasil menghapus tags");
        }else {
            System.out.println("tags kosong tidak dapat menghapus!");
        }
    }
}
