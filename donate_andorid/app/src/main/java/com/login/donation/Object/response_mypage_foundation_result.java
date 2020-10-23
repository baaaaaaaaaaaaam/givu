package com.login.donation.Object;

public class response_mypage_foundation_result {
    String result;
    String campaign;
    String foundation_member;
    String volunteer_recruitment;
    String time_campaign;

    public response_mypage_foundation_result(String result, String campaign, String foundation_member, String volunteer_recruitment, String time_campaign) {
        this.result = result;
        this.campaign = campaign;
        this.foundation_member = foundation_member;
        this.volunteer_recruitment = volunteer_recruitment;
        this.time_campaign = time_campaign;
    }

    public String getResult() {
        return result;
    }

    public String getCampaign() {
        return campaign;
    }

    public String getFoundation_member() {
        return foundation_member;
    }

    public String getVolunteer_recruitment() {
        return volunteer_recruitment;
    }

    public String getTime_campaign() {
        return time_campaign;
    }
}
