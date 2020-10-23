package com.login.donation.Object;

public class response_mypage_donate_result {
    String result;
    String count;
    String sum;
    String account;
    String time;
    String response_mypage_donate;
    String response_mypage_donate_volunteer;
    String response_mypage_donate_time;
    String response_mypage_challenge;

    public response_mypage_donate_result(String result, String count, String sum, String account, String time, String response_mypage_donate, String response_mypage_donate_volunteer, String response_mypage_donate_time, String response_mypage_challenge) {
        this.result = result;
        this.count = count;
        this.sum = sum;
        this.account = account;
        this.time = time;
        this.response_mypage_donate = response_mypage_donate;
        this.response_mypage_donate_volunteer = response_mypage_donate_volunteer;
        this.response_mypage_donate_time = response_mypage_donate_time;
        this.response_mypage_challenge = response_mypage_challenge;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getResponse_mypage_donate() {
        return response_mypage_donate;
    }

    public void setResponse_mypage_donate(String response_mypage_donate) {
        this.response_mypage_donate = response_mypage_donate;
    }

    public String getResponse_mypage_donate_volunteer() {
        return response_mypage_donate_volunteer;
    }

    public void setResponse_mypage_donate_volunteer(String response_mypage_donate_volunteer) {
        this.response_mypage_donate_volunteer = response_mypage_donate_volunteer;
    }

    public String getResponse_mypage_donate_time() {
        return response_mypage_donate_time;
    }

    public void setResponse_mypage_donate_time(String response_mypage_donate_time) {
        this.response_mypage_donate_time = response_mypage_donate_time;
    }

    public String getResponse_mypage_challenge() {
        return response_mypage_challenge;
    }

    public void setResponse_mypage_challenge(String response_mypage_challenge) {
        this.response_mypage_challenge = response_mypage_challenge;
    }
}
