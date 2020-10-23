package com.login.donation.Object;

public class campaign_write {

    String addId;
    String addImagePath;

    public campaign_write(String addId, String addImagePath) {
        this.addId = addId;
        this.addImagePath = addImagePath;
    }

    public String getAddId() {
        return addId;
    }

    public String getAddImagePath() {
        return addImagePath;
    }

    public void setAddId(String addId) {
        this.addId = addId;
    }

    public void setAddImagePath(String addImagePath) {
        this.addImagePath = addImagePath;
    }
}
