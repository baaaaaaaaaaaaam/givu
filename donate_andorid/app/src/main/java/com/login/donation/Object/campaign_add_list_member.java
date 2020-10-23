package com.login.donation.Object;


//이 클래스는 add_share_listDialog 액티비티에서 리스트에 보여줄 객체를 담는 클래스이다
// 서버에서받아온 queryRsult의 값을 Gson으로 파싱해서 이클래스 객체로 만든 후 list로 만든다
// 이 객체는 멤버 아이디와 이미지 경로 뿐아니라 check라는 boolean을 가지게 되는데 그 이유는
// 리스트로 보여줄때 체크박스가 존재 하기 떄문에다.
// 체크박스를 체크된 아이템들은 boolean 값을 true로 변경하고 write_campaignActivity로 전달 된다.


public class campaign_add_list_member {
    String memberid;
    String imagepath;
    boolean check;

    public campaign_add_list_member(String memberid, String imagepath, boolean check) {
        this.memberid = memberid;
        this.imagepath = imagepath;
        this.check = check;
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

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
