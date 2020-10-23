package com.login.donation.Object;

public class response_mypage_donate {
    String campaign_seq;
    String donate_money;
    String writer;
    String subject;
    String startDate;
    String endDate;
    String imagePath;


    public response_mypage_donate(String campaign_seq, String donate_money, String writer, String subject, String startDate, String endDate, String imagePath) {
        this.campaign_seq = campaign_seq;
        this.donate_money = donate_money;
        this.writer = writer;
        this.subject = subject;
        this.startDate = startDate;
        this.endDate = endDate;
        this.imagePath = imagePath;
    }


    public String getCampaign_seq() {
        return campaign_seq;
    }

    public void setCampaign_seq(String campaign_seq) {
        this.campaign_seq = campaign_seq;
    }

    public String getDonate_money() {
        return donate_money;
    }

    public void setDonate_money(String donate_money) {
        this.donate_money = donate_money;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
