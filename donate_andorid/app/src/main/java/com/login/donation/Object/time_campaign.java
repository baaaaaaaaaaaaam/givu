package com.login.donation.Object;

public class time_campaign {

    String seq;
    String id;
    String subject;
    String imagePath;
    String money;
    int currentTime;
    String time;
    String donation;
    String startDate;
    String endDate;
    String content;
    String doing;
    String mission;
    String permission;

    public time_campaign(String seq, String id, String subject, String imagePath, String money, int currentTime, String time, String donation, String startDate, String endDate, String content, String doing, String mission, String permission) {
        this.seq = seq;
        this.id = id;
        this.subject = subject;
        this.imagePath = imagePath;
        this.money = money;
        this.currentTime = currentTime;
        this.time = time;
        this.donation = donation;
        this.startDate = startDate;
        this.endDate = endDate;
        this.content = content;
        this.doing = doing;
        this.mission = mission;
        this.permission = permission;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public int getCurrent_time() {
        return currentTime;
    }

    public void setCurrent_time(int currentTime) {
        this.currentTime = currentTime;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDonation() {
        return donation;
    }

    public void setDonation(String donation) {
        this.donation = donation;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDoing() {
        return doing;
    }

    public void setDoing(String doing) {
        this.doing = doing;
    }

    public String getMission() {
        return mission;
    }

    public void setMission(String mission) {
        this.mission = mission;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
