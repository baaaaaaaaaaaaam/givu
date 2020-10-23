package com.login.donation.Object;

public class response_time_campaign_detail_page {


    String result;
    String time_campaign;
    String joiner;
    String share_list;

    public response_time_campaign_detail_page(String result, String time_campaign, String joiner, String share_list) {
        this.result = result;
        this.time_campaign = time_campaign;
        this.joiner = joiner;
        this.share_list = share_list;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getTime_campaign() {
        return time_campaign;
    }

    public void setTime_campaign(String time_campaign) {
        this.time_campaign = time_campaign;
    }

    public String getJoiner() {
        return joiner;
    }

    public void setJoiner(String joiner) {
        this.joiner = joiner;
    }

    public String getShare_list() {
        return share_list;
    }

    public void setShare_list(String share_list) {
        this.share_list = share_list;
    }
}
