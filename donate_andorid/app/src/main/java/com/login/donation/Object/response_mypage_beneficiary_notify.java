package com.login.donation.Object;

import androidx.annotation.NonNull;

public class response_mypage_beneficiary_notify {

    String docType;
    String seq;
    String money;
    String startDate;
    String endDate;
    String subject;


    public response_mypage_beneficiary_notify(String docType, String seq, String money, String startDate, String endDate, String subject) {
        this.docType = docType;
        this.seq = seq;
        this.money = money;
        this.startDate = startDate;
        this.endDate = endDate;
        this.subject = subject;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDocType() {
        return docType;
    }

    public String getSeq() {
        return seq;
    }

    public String getMoney() {
        return money;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getSubject() {
        return subject;
    }

    @NonNull
    @Override
    public String toString() {
        return startDate + " 부터 "+ endDate + " 까지 진행된 \"" +subject + "\" 캠페인이 종료되어 "+money + " 원을 나눔 받았습니다";
    }
}
