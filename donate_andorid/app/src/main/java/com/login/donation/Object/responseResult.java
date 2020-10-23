package com.login.donation.Object;

//서버에서 단순 result 난 받기위해 사용되는 클래스이다.


public class responseResult {
    String result;

    public responseResult(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
