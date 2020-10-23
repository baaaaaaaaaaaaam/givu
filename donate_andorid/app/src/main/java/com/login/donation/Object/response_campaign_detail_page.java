package com.login.donation.Object;

public class response_campaign_detail_page {

    String result;
    String campaign;
    String donate_list;
    String share_list;

    public response_campaign_detail_page(String result, String campaign, String donate_list, String share_list) {
        this.result = result;
        this.campaign = campaign;
        this.donate_list = donate_list;
        this.share_list = share_list;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public String getDonate_list() {
        return donate_list;
    }

    public void setDonate_list(String donate_list) {
        this.donate_list = donate_list;
    }

    public String getShare_list() {
        return share_list;
    }

    public void setShare_list(String share_list) {
        this.share_list = share_list;
    }
}
