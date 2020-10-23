package com.login.donation.Object;

public class response_mypage_beneficiary_result {

    String result;
    String account;
    String groupid;
    String campaign;
    String share_inblock;
    String time_campaign;
    String mission_share_inblock;

    public response_mypage_beneficiary_result(String result, String account, String groupid, String campaign, String share_inblock, String time_campaign, String mission_share_inblock) {
        this.result = result;
        this.account = account;
        this.groupid = groupid;
        this.campaign = campaign;
        this.share_inblock = share_inblock;
        this.time_campaign = time_campaign;
        this.mission_share_inblock = mission_share_inblock;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public String getShare_inblock() {
        return share_inblock;
    }

    public void setShare_inblock(String share_inblock) {
        this.share_inblock = share_inblock;
    }

    public String getTime_campaign() {
        return time_campaign;
    }

    public void setTime_campaign(String time_campaign) {
        this.time_campaign = time_campaign;
    }

    public String getMission_share_inblock() {
        return mission_share_inblock;
    }

    public void setMission_share_inblock(String mission_share_inblock) {
        this.mission_share_inblock = mission_share_inblock;
    }
}
