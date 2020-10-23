package com.login.donation.Object;

public class Image_and_id_object {
    String memberid;
    String imagepath;

    public Image_and_id_object(String memberid, String imagepath) {
        this.memberid = memberid;
        this.imagepath = imagepath;
    }

    public String getMemberid() {
        return memberid;
    }

    public void setMemberid(String memberid) {
        this.memberid = memberid;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }
}
