package com.login.donation.Object;

public class response_mypage_donate_volunteer {
    String campaign;
    String time;
    volunteer_recruitment volunteer;

    public response_mypage_donate_volunteer( String time, volunteer_recruitment volunteer) {

        this.time = time;
        this.volunteer = volunteer;
    }



    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public volunteer_recruitment getVolunteer() {
        return volunteer;
    }

    public void setVolunteer(volunteer_recruitment volunteer) {
        this.volunteer = volunteer;
    }
}
