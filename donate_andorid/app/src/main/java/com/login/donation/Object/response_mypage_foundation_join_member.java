package com.login.donation.Object;

public class response_mypage_foundation_join_member {

    String memberid;
    String imagepath;

    public response_mypage_foundation_join_member(String memberid, String imagepath) {
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
